package bayern.jugin.k8s.crd;

import io.fabric8.kubernetes.model.annotation.PrinterColumn;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UnsecretSpecV1Beta1 {
    @PrinterColumn
    private String sourceSecret;

    @PrinterColumn
    private String targetConfigmap;

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
}
