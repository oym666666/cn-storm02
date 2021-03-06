package timewindow;

import computer.FileBolt;
import computer.PrintBolt;
import computer.ReadFileSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

public class WindowTopology {
    public static void main(String[] args) {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("spout",new WindowSpout(),1);
//      topologyBuilder.setBolt("filebolt",new FileBolt(),1).setNumTasks(4).shuffleGrouping("spout");

          //记住实时统计的过程中 千万不能用并发，这里如果加了并发，就会导致数据紊乱，阶段数据的处理，一定是单线程去操作，否则会导致过多的计算
 //       topologyBuilder.setBolt("windowBolt",new MyTumplingWindow().withTumblingWindow(BaseWindowedBolt.Count.of(5))).shuffleGrouping("spout");
        topologyBuilder.setBolt("windowBolt",new MySlidingWindow().withWindow(BaseWindowedBolt.Count.of(5),BaseWindowedBolt.Count.of(3))).shuffleGrouping("spout");
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
            } catch (AlreadyAliveException | InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
        }else{
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("windowTopology",config,topologyBuilder.createTopology());
        }

    }
}

