package bayern.jugin.k8s;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bayern.jugin.k8s.crd.UnsecretSpecV1Beta2;
import bayern.jugin.k8s.crd.UnsecretStatusV1Beta2;
import bayern.jugin.k8s.crd.UnsecretV1Beta2;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class Unsecret0r {
    static final String UNSECRET_CMD_ADDRESS = "unsecet-cmd";

    private static Logger log = LoggerFactory.getLogger(Unsecret0r.class);

    private final KubernetesClient kube;

    public Unsecret0r(KubernetesClient kube) {
        this.kube = kube;
    }

    @ConsumeEvent(UNSECRET_CMD_ADDRESS)
    public void unsecret(UnsecretCommand cmd) {
        var spec = cmd.spec;
        log.info("Unsecreting {}.{}", cmd.namespace, spec.getSourceSecret());

        var secret = kube.secrets().inNamespace(cmd.namespace).withName(spec.getSourceSecret()).get();
        if (secret != null) {

            var cmb = new ConfigMapBuilder()
                .withNewMetadata()
                .withName(spec.getTargetConfigmap())
                .withNamespace(cmd.namespace);

            if (spec.getConfigmapLabelKey() != null && spec.getConfigmapLabelValue() != null) {
                cmb = cmb.withLabels(Map.of(spec.getConfigmapLabelKey(), spec.getConfigmapLabelValue()));
            }

            var unsecreted = new HashMap<String, String>();
            secret.getData().forEach((k, v) -> {
                unsecreted.put(k, new String(Base64.getDecoder().decode(v)));
            });

            var cm = cmb.endMetadata()
                .withData(unsecreted).build();

            kube.configMaps().createOrReplace(cm);
        }

        var unsecretResource = kube.resources(UnsecretV1Beta2.class).inNamespace(cmd.namespace).withName(cmd.name);
        var unsecret = unsecretResource.get();

        var status = new UnsecretStatusV1Beta2();
        status.setLastUpdate(Instant.now().toString());

        if (secret != null) {
            status.setValueCount(secret.getData().size());
        } else {
            status.setValueCount(0);
            status.setMessage("Secret missing: " + cmd.spec.getSourceSecret());
        }
        unsecret.setStatus(status);

        unsecretResource.replaceStatus(unsecret);
    }
}

class UnsecretCommand {
    final String name, namespace;
    final UnsecretSpecV1Beta2 spec;

    UnsecretCommand(String name, String namespace, UnsecretSpecV1Beta2 spec) {
        this.name = name;
        this.namespace = namespace;
        this.spec = spec;
    }
}
