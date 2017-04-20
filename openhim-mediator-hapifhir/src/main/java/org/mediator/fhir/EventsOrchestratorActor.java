package org.mediator.fhir;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.phloc.commons.base64.Base64;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.*;
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsOrchestratorActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final MediatorConfig config;
    private MediatorFhirConfig mediatorConfiguration;
    private ResolveEventRequest originalRequest;

    public static class ResolveEventRequest extends SimpleMediatorRequest <String>{
        public ResolveEventRequest(ActorRef requestHandler, ActorRef respondTo, String requestObject) {
            super(requestHandler, respondTo, requestObject);
        }
    }

    public static class ResolveEventResponse extends SimpleMediatorResponse <String>{
        public ResolveEventResponse (MediatorRequestMessage originalRequest, String responseObject) {
            super(originalRequest, responseObject);
        }
    }

    public EventsOrchestratorActor(MediatorConfig config) {
        this.config = config;
        this.mediatorConfiguration=new MediatorFhirConfig();
    }

    private void saveEvent(ResolveEventRequest request)
    {
        originalRequest = request;
        String authentication=mediatorConfiguration.getAuthentication();
        String authEncoded = Base64.encodeBytes(authentication.getBytes());
        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        Map <String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type","application/json");
        headers.put("Authorization","Basic "+authEncoded);
        String resourceInformation="Dhis2 event resources to push";
        log.info("Querying the HAPI Test servers");


        String builtRequestPath="";
        builtRequestPath="/events";
        String paramsRequest="";
        paramsRequest=request.getRequestObject();
        String requestBody=paramsRequest;


        //builtRequestPath=FhirMediatorUtilities.buildResourcesSearchRequestByIds("Condition",paramsRequest);
        //builtRequestPath=request.getRequestObject();
        String ServerApp="";
        String baseServerRepoURI="";
        try
        {
            ServerApp=mediatorConfiguration.getServerTrackerAppName().equals("null")?null:mediatorConfiguration.getServerTrackerAppName();
            baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                    this.mediatorConfiguration.getServerTrackerScheme(),
                    this.mediatorConfiguration.getServerTrackerURI(),
                    this.mediatorConfiguration.getServerTrackerPort(),
                    ServerApp,
                    this.mediatorConfiguration.getServerTrackerFhirDataModel()
            );

        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //throw new Exception(exc.getMessage());
            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                    exc.getMessage(),"Error");
            return;
        }


        //String uriRepServer=baseServerRepoURI+"/Practitioner?_lastUpdated=%3E=2016-10-11T09:12:37&_lastUpdated=%3C=2016-10-13T09:12:45&_pretty=true";
        String uriRepServer=baseServerRepoURI+builtRequestPath;

        String encodedUriSourceServer=FhirMediatorUtilities.encodeUrlToHttpFormat(uriRepServer);
        MediatorHTTPRequest serviceRequest = new MediatorHTTPRequest(
                request.getRequestHandler(),
                getSelf(),
                resourceInformation,
                "POST",
                encodedUriSourceServer,
                requestBody,
                headers,
                null);

        httpConnector.tell(serviceRequest, getSelf());

    }

    private void processEventResponse(MediatorHTTPResponse response) {
        log.info("Received response Fhir repository Server");
        //originalRequest.getRespondTo().tell(response.toFinishRequest(), getSelf());
        //Perform the resource validation from the response
        try
        {
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                StringBuilder strResponse=new StringBuilder();
                //Copy the response Char by char to avoid the string size limitation issues
                strResponse.append(response.getBody());
                ResolveEventResponse actorResponse=new ResolveEventResponse(originalRequest,strResponse.toString());
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
        if (msg instanceof ResolveEventRequest) {
            saveEvent((ResolveEventRequest) msg);
        }
        else if(msg instanceof MediatorHTTPResponse)
        {
            processEventResponse((MediatorHTTPResponse) msg);
        }
        else
        {
            unhandled(msg);
        }
    }
}
