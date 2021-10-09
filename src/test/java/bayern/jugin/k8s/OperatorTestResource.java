package bayern.jugin.k8s;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class OperatorTestResource implements QuarkusTestResourceLifecycleManager {

    final String crdFile = "target/kubernetes/unsecrets.jug-in.bayern-v1.yml";
    final KubernetesClient k8sClient = new DefaultKubernetesClient();
    final ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> crd;

    {
        // fail if not connected to a local k8s to avoid mistakenly breaking production clusters
        assertTrue(
            List.of("127.0.0.1", "localhost", "::1")
                .contains(k8sClient.getMasterUrl().getHost())
        );
        try {
            crd = k8sClient.load(new FileInputStream(crdFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> start() {
        crd.createOrReplace().get(0);
        crd.waitUntilReady(10, TimeUnit.SECONDS);
        return Map.of();
    }

    @Override
    public void stop() {
        crd.delete();
    }
}
