package moneytransferapp;

import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.interceptors.*;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Collections;

@Slf4j
public class MoneyTransferWorker {

    public static void main(String[] args) {

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubsOptions workflowServiceStubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setTarget("100.91.145.58:7233")
                .build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(workflowServiceStubsOptions);

        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setNamespace("animesh-dev")
                .setContextPropagators(Collections.singletonList(new TraceContextPropagator()))
                .build();
        WorkflowClient client = WorkflowClient.newInstance(service, clientOptions);

        // Worker factory is used to create Workers that poll specific Task Queues.
        WorkerFactoryOptions options = WorkerFactoryOptions.newBuilder()
                .setWorkerInterceptors(new SignalWorkerInterceptor())
                .build();
        WorkerFactory factory = WorkerFactory.newInstance(client, options);
        Worker worker = factory.newWorker(Shared.MONEY_TRANSFER_TASK_QUEUE);

        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        // Activities are stateless and thread safe so a shared instance is used.
        worker.registerActivitiesImplementations(new AccountActivityImpl());

        // Start listening to the Task Queue.
        factory.start();
    }
}

@Slf4j
class SignalWorkerInterceptor implements WorkerInterceptor {
    @Override
    public WorkflowInboundCallsInterceptor interceptWorkflow(WorkflowInboundCallsInterceptor next) {
        return new SignalWorkflowInboundCallsInterceptor(next);
    }

    @Override
    public ActivityInboundCallsInterceptor interceptActivity(ActivityInboundCallsInterceptor next) {
        return next;
    }
}

@Slf4j
class SignalWorkflowInboundCallsInterceptor extends WorkflowInboundCallsInterceptorBase {
    public SignalWorkflowInboundCallsInterceptor(WorkflowInboundCallsInterceptor next) {
        super(next);
    }

    @Override
    public void handleSignal(SignalInput input) {
        log.info("(Intercepted) Handle Signal {}", input.getSignalName());
        super.handleSignal(input);
    }
}