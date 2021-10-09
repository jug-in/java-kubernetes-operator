package bayern.jugin.k8s;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bayern.jugin.k8s.crd.UnsecretStatusV1Beta2;
import bayern.jugin.k8s.crd.UnsecretV1Beta2;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.Controller;
import io.javaoperatorsdk.operator.api.DeleteControl;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.api.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.EventSourceManager;
import io.quarkus.runtime.Startup;
import io.vertx.core.eventbus.EventBus;

@Startup
@Controller
public class UnsecretController implements ResourceController<UnsecretV1Beta2> {

    private static Logger log = LoggerFactory.getLogger(UnsecretController.class);

    private final EventBus eventBus;

    public UnsecretController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public DeleteControl deleteResource(UnsecretV1Beta2 resource, Context<UnsecretV1Beta2> context) {
        log.info("Deleting beta1 {}", resource.getMetadata().getName());
        return DeleteControl.DEFAULT_DELETE; // DeleteControl.NO_FINALIZER_REMOVAL
    }

    @Override
    public UpdateControl<UnsecretV1Beta2> createOrUpdateResource(UnsecretV1Beta2 resource, Context<UnsecretV1Beta2> context) {
        log.info("Updated beta1 {}", resource.getMetadata().getName());

        eventBus.send(Unsecret0r.UNSECRET_CMD_ADDRESS,
            new UnsecretCommand(
                resource.getMetadata().getName(),
                resource.getMetadata().getNamespace(),
                resource.getSpec()
            ));

        var status = new UnsecretStatusV1Beta2();
        status.setLastUpdate(Instant.now().toString());

        resource.setStatus(status);

        return UpdateControl.updateStatusSubResource(resource); // UpdateControl.noUpdate()
    }

    @Override
    public void init(EventSourceManager eventSourceManager) {
        log.info("Initializing controller");
    }
}
