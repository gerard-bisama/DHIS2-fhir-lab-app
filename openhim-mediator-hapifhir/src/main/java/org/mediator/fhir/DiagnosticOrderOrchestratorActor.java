package org.mediator.fhir;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosticOrderOrchestratorActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final MediatorConfig config;
    private MediatorFhirConfig mediatorConfiguration;
    private ResolveDiagnosticOrderRequest originalRequest;

    public static class ResolveDiagnosticOrderRequest extends SimpleMediatorRequest <List<String>>{
        public ResolveDiagnosticOrderRequest(ActorRef requestHandler, ActorRef respondTo, List<String> requestObject) {
            super(requestHandler, respondTo, requestObject);
        }
    }

    public static class ResolveDiagnosticOrderResponse extends SimpleMediatorResponse <String>{
        public ResolveDiagnosticOrderResponse (MediatorRequestMessage originalRequest, String responseObject) {
            super(originalRequest, responseObject);
        }
    }

    public DiagnosticOrderOrchestratorActor(MediatorConfig config) {
        this.config = config;
        this.mediatorConfiguration=new MediatorFhirConfig();
    }

    private void queryDiagnosticOrder(ResolveDiagnosticOrderRequest request)
    {
        originalRequest = request;
        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        Map <String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        String resourceInformation="FHIR Diagnostic Order resources";
        log.info("Querying the HAPI Test servers");


        String builtRequestPath="";
        List<String> paramsRequest=new ArrayList<>();
        paramsRequest=request.getRequestObject();


        builtRequestPath=FhirMediatorUtilities.buildResourcesSearchRequestByIds("DiagnosticOrder",paramsRequest);
        //builtRequestPath=request.getRequestObject();
        String ServerApp="";
        String baseServerRepoURI="";
        try
        {
            ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
            baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                    this.mediatorConfiguration.getServerTargetscheme(),
                    this.mediatorConfiguration.getServerTargetURI(),
                    this.mediatorConfiguration.getServerTargetPort(),
                    ServerApp,
                    this.mediatorConfiguration.getServerTargetFhirDataModel()
            );

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                    exc.getMessage(),"Error");
            log.error(exc.getMessage());
            return;
        }


        //String uriRepServer=baseServerRepoURI+"/Practitioner?_lastUpdated=%3E=2016-10-11T09:12:37&_lastUpdated=%3C=2016-10-13T09:12:45&_pretty=true";
        String uriRepServer=baseServerRepoURI+builtRequestPath;

        String encodedUriSourceServer=FhirMediatorUtilities.encodeUrlToHttpFormat(uriRepServer);
        MediatorHTTPRequest serviceRequest = new MediatorHTTPRequest(
                request.getRequestHandler(),
                getSelf(),
                resourceInformation,
                "GET",
                encodedUriSourceServer,
                null,
                headers,
                null);

        httpConnector.tell(serviceRequest, getSelf());

    }

    private void processQueryDiagnosticOrderResponse(MediatorHTTPResponse response) {
        log.info("Received response Fhir repository Server");
        //originalRequest.getRespondTo().tell(response.toFinishRequest(), getSelf());
        //Perform the resource validation from the response
        try
        {
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                StringBuilder strResponse=new StringBuilder();
                //Copy the response Char by char to avoid the string size limitation issues
                strResponse.append(response.getBody());
                ResolveDiagnosticOrderResponse actorResponse=new ResolveDiagnosticOrderResponse(originalRequest,strResponse.toString());
                originalRequest.getRespondTo().tell(actorResponse, getSelf());
            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                    exc.getMessage(),"Error");
            log.error(exc.getMessage());
            return;
        }

    }
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof ResolveDiagnosticOrderRequest) {
            queryDiagnosticOrder((ResolveDiagnosticOrderRequest) msg);
        }
        else if(msg instanceof MediatorHTTPResponse)
        {
            processQueryDiagnosticOrderResponse((MediatorHTTPResponse) msg);
        }
        else
        {
            unhandled(msg);
        }
    }
}
