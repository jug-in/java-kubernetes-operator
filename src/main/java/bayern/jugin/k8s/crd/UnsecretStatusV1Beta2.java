package bayern.jugin.k8s.crd;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UnsecretStatusV1Beta2 {
    private Integer valueCount;
    private String lastUpdate;
    private String message;

    public Integer getValueCount() {
        return valueCount;
    }

    public void setValueCount(Integer valueCount) {
        this.valueCount = valueCount;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
