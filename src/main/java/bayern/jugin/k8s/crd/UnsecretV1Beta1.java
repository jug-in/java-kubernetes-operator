package bayern.jugin.k8s.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("jug-in.bayern")
@Version(value = "v1beta1", storage = false)
@Kind("Unsecret")
@Singular("Unsecret")
@Plural("Unsecrets")
@ShortNames("us")
public class UnsecretV1Beta1
    extends CustomResource<UnsecretSpecV1Beta1, UnsecretStatusV1Beta2>
    implements Namespaced {
}
