package bayern.jugin.k8s;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import bayern.jugin.k8s.crd.UnsecretSpecV1Beta2;
import bayern.jugin.k8s.crd.UnsecretV1Beta2;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(OperatorTestResource.class)
public class UnsecretTest {

    @Inject
    KubernetesClient kube;

    @Test
    public void testSecretToConfigmap() throws Exception {
        var secret = new SecretBuilder().withNewMetadata().withName("test-secret").endMetadata().withStringData(Map.of("key1", "value1", "key2", "value2")).build();
        kube.secrets().inNamespace("default").createOrReplace(secret);

        var crd = new UnsecretV1Beta2();
        var meta = new ObjectMeta();
        meta.setName("show-me");
        crd.setMetadata(meta);
        var spec = new UnsecretSpecV1Beta2();
        spec.setSourceSecret("test-secret");
        spec.setTargetConfigmap("test-cm");
        crd.setSpec(spec);
        kube.resources(UnsecretV1Beta2.class).inNamespace("default").create(crd);

        var cm = kube.configMaps().inNamespace("default").withName("test-cm").waitUntilReady(10, TimeUnit.SECONDS);

        assertEquals("value1", cm.getData().get("key1"));
        assertEquals("value2", cm.getData().get("key2"));
    }

    @AfterAll
    public static void cleanup() {
        final KubernetesClient k8sClient = new DefaultKubernetesClient();
        k8sClient.configMaps().inNamespace("default").withName("test-cm").delete();
        k8sClient.resources(UnsecretV1Beta2.class).inNamespace("default").delete();
    }
}
