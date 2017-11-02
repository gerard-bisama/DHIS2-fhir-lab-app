package org.mediator.fhir;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openhim.mediator.engine.*;
//import scala.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MediatorMain {

    private static RoutingTable buildRoutingTable() throws RoutingTable.RouteAlreadyMappedException {
        RoutingTable routingTable = new RoutingTable();

        //TODO Configure routes here
        //...
        //routingTable.addRoute("Practitioners", DefaultOrchestrator.class);
        routingTable.addRegexRoute("/csvfhir/.*", CSVFhirOrchestrator.class);
        routingTable.addRegexRoute("/pushfhir/.*", FhirDHISOrchestrator.class);
        routingTable.addRegexRoute("/entitytrackers/.*", DefaultOrchestrator.class);


        return routingTable;
    }

    private static StartupActorsConfig buildStartupActorsConfig() {
        StartupActorsConfig startupActors = new StartupActorsConfig();

        //TODO Add own startup actors here
        //...

        return startupActors;
    }

    private static MediatorConfig loadConfig(String configPath) throws IOException, RoutingTable.RouteAlreadyMappedException {
        MediatorConfig config = new MediatorConfig();
        //MediatorFhirConfig config=new MediatorFhirConfig();

        if (configPath!=null) {
            Properties props = new Properties();
            File conf = new File(configPath);
            InputStream in = FileUtils.openInputStream(conf);
            props.load(in);
            IOUtils.closeQuietly(in);

            config.setProperties(props);
        } else {
            config.setProperties("mediator.properties");
        }

        config.setName(config.getProperty("mediator.name"));
        config.setServerHost(config.getProperty("mediator.host"));
        config.setServerPort( Integer.parseInt(config.getProperty("mediator.port")) );
        config.setRootTimeout(Integer.parseInt(config.getProperty("mediator.timeout")));


        config.setCoreHost(config.getProperty("core.host"));
        config.setCoreAPIUsername(config.getProperty("core.api.user"));
        config.setCoreAPIPassword(config.getProperty("core.api.password"));
        if (config.getProperty("core.api.port") != null) {
            config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        }

        config.setRoutingTable(buildRoutingTable());
        config.setStartupActors(buildStartupActorsConfig());

        InputStream regInfo = MediatorMain.class.getClassLoader().getResourceAsStream("mediator-registration-info.json");
        RegistrationConfig regConfig = new RegistrationConfig(regInfo);
        config.setRegistrationConfig(regConfig);

        if (config.getProperty("mediator.heartbeats")!=null && "true".equalsIgnoreCase(config.getProperty("mediator.heartbeats"))) {
            config.setHeartbeatsEnabled(true);
        }

        return config;
    }

    public static void main(String... args) throws Exception {
        //setup actor system
        final ActorSystem system = ActorSystem.create("mediator");
        //setup logger for main
        final LoggingAdapter log = Logging.getLogger(system, "main");

        //setup actors
        log.info("Initializing mediator actors...");

        String configPath = null;
        if (args.length==2 && args[0].equals("--conf")) {
            configPath = args[1];
            log.info("Loading mediator configuration from '" + configPath + "'...");
        } else {
            log.info("No configuration specified. Using default properties...");
        }

        MediatorConfig config = loadConfig(configPath);
        final MediatorServer server = new MediatorServer(system, config);

        //setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down mediator");
                server.stop();
                system.shutdown();
            }
        });

        log.info("Starting mediator server...");
        server.start();

        log.info(String.format("%s listening on %s:%s", config.getName(), config.getServerHost(), config.getServerPort()));
        Thread.currentThread().join();
    }
}
