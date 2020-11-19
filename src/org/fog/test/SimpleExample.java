package org.fog.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.FogBroker;
import org.fog.entities.PhysicalTopology;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.utils.JsonToTopology;

public class SimpleExample {

    public static void main(String[] args) {

        Log.printLine("Starting SimpleExample...");

        try {
            Log.disable();

            CloudSim.init(1, null, true);

            String appId = "simpleExample";
            FogBroker broker = new FogBroker("broker");

            Application application = createApplication(appId, broker.getId());
            application.setUserId(broker.getId());

            PhysicalTopology physicalTopology = JsonToTopology.getPhysicalTopology(broker.getId(), appId, "../topologies/simpleExample.json");

            Controller controller = new Controller("controller",
                                                   physicalTopology.getFogDevices(),
                                                   physicalTopology.getSensors(),
                                                   physicalTopology.getActuators());

            controller.submitApplication(application, 0, new ModulePlacementEdgewards(physicalTopology.getFogDevices(),
                                                                                      physicalTopology.getSensors(),
                                                                                      physicalTopology.getActuators(),
                                                                                      application,
                                                                                      ModuleMapping.createModuleMapping()));

            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            Log.printLine("SimpleExample finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }


    @SuppressWarnings({ "serial" })
    private static Application createApplication(String appId, int userId){

        Application application = Application.createApplication(appId, userId);
        application.addAppModule("process", 10);

        application.addTupleMapping("process", "TEMP", "MOTOR", new FractionalSelectivity(1.0));

        application.addAppEdge("TEMP", "process", 1000, 100, "TEMP", Tuple.UP, AppEdge.SENSOR);
        // application.addAppEdge("process", "MOTOR", 1000, 100, "ACTUATOR", Tuple.DOWN, AppEdge.ACTUATOR);

        // application.addAppEdge("process", "ACTUATOR", 1000, 100, "ACTUATOR", Tuple.UP, AppEdge.SENSOR);
        // application.addAppEdge("client", "classifier", 8000, 100, "_SENSOR", Tuple.UP, AppEdge.MODULE);
        // application.addAppEdge("classifier", "tuner", 1000000, 100, "HISTORY", Tuple.UP, AppEdge.MODULE);
        // application.addAppEdge("classifier", "client", 1000, 100, "CLASSIFICATION", Tuple.DOWN, AppEdge.MODULE);
        // application.addAppEdge("tuner", "classifier", 1000, 100, "TUNING_PARAMS", Tuple.DOWN, AppEdge.MODULE);


        final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("TEMP");}});
        List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};

        application.setLoops(loops);

        return application;
    }
}
