package computer;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

public class FileCountTopology {
    public static void main(String[] args) {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("spout",new ReadFileSpout(),1);
        topologyBuilder.setBolt("filebolt",new FileBolt(),1).shuffleGrouping("spout");
        topologyBuilder.setBolt("printbolt",new PrintBolt(),1).shuffleGrouping("filebolt");
//        topologyBuilder.setBolt("printbolt",new PrintBolt(),1).shuffleGrouping("filebolt");
//        topologyBuilder.setBolt("printbolt",new PrintBolt(),1).shuffleGrouping("filebolt");
//        topologyBuilder.setBolt("printbolt",new PrintBolt(),1).shuffleGrouping("filebolt");


        Config config = new Config();
        config.setDebug(true);
        config.setNumWorkers(3); //规则



        if(args.length>0){
            //提交 到集群
            try {
                StormSubmitter.submitTopology(args[0],config,topologyBuilder.createTopology());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
        }else{
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("firstTopology",config,topologyBuilder.createTopology());
        }

    }
}
