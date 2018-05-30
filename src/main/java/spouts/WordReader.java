package spouts;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;


/**
 * spout ----文本----> bolt
 * WordReader负责从文件按行读取文本，并把文本行提供给第一个 bolt
 */
public class WordReader extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;

    public void ack(Object msgId) {
        System.out.println("OK:" + msgId);
    }

    public void close() {
    }

    public void fail(Object msgId) {
        System.out.println("FAIL:" + msgId);
    }

    /**
     * The only thing that the methods will do It is emit each
     * file line
     * <p>
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     */
    public void nextTuple() {
        /**
         * The nextuple it is called forever, so if we have been readed the file
         * we will wait and then return
         *
         * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
         */
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Do nothing
            }
            return;
        }
        String str;
        //Open the reader 创建 reader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            //Read all lines 读取所有的文本行
            while ((str = reader.readLine()) != null) {
                /**
                 * By each line emmit a new value with the line as a their
                 * 按行发布一个新值
                 */
                this.collector.emit(new Values(str), str);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }
    }

    /**
     * We will create the file and get the collector object
     * 我们将创建一个文件并维持一个collector对象
     * <p>
     * 第一个被调用的spout方法都是public void open(Map conf, TopologyContext context, SpoutOutputCollector collector)。它接收如下参数：配置对象，在定义topology对象是创建；TopologyContext对象，包含所有拓扑数据；还有SpoutOutputCollector对象，它能让我们发布交给bolts处理的数据。
     */
    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        try {
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
        }
        this.collector = collector;
    }

    /**
     * Declare the output field "word"
     * 声明输入域"word"
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }


}
