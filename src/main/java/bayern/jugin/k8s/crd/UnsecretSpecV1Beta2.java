package bayern.jugin.k8s.crd;

import io.fabric8.kubernetes.model.annotation.PrinterColumn;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UnsecretSpecV1Beta2 {
    @PrinterColumn
    private String sourceSecret;

    @PrinterColumn
    private String targetConfigmap;

    private String configmapLabelKey;
    private String configmapLabelValue;

    public String getSourceSecret() {
        return sourceSecret;
    }

    public void setSourceSecret(String sourceSecret) {
        this.sourceSecret = sourceSecret;
    }

    public String getTargetConfigmap() {
        return targetConfigmap;
    }

    public void setTargetConfigmap(String targetConfigmap) {
        this.targetConfigmap = targetConfigmap;
    }

    public String getConfigmapLabelKey() {
        return configmapLabelKey;
    }

    public void setConfigmapLabelKey(String configmapLabelKey) {
        this.configmapLabelKey = configmapLabelKey;
    }

    public String getConfigmapLabelValue() {
        return configmapLabelValue;
    }

    public void setConfigmapLabelValue(String configmapLabelValue) {
        this.configmapLabelValue = configmapLabelValue;
    }
}
