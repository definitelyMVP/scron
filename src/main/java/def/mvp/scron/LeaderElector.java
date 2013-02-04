package def.mvp.scron;

import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author defMVP
 * @date 2013-02-04
 */
public class LeaderElector {

    private static final Logger log = LoggerFactory.getLogger(LeaderElector.class);

    private String zkConnectionString;
    private String ephemeralZnodeNamePrefix;
    private String ephemeralZnodeParent;
    private String path;
    private int zkSessionTimeout;
    private ZooKeeper zkClient;
    private boolean registered = false;

    public LeaderElector(String zkConnectionString, int zkSessionTimeout, String ephemeralZnodeParent, String ephemeralZnodeNamePrefix) {
        this.zkConnectionString = zkConnectionString;
        this.zkSessionTimeout = zkSessionTimeout;
        this.ephemeralZnodeParent = ephemeralZnodeParent;
        this.ephemeralZnodeNamePrefix = ephemeralZnodeNamePrefix;
        this.path = ephemeralZnodeParent + "/" + ephemeralZnodeNamePrefix;
    }

    public void register() {
        if (registered) {
            return;
        }
        try {
            this.zkClient = new ZooKeeper(zkConnectionString, zkSessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {

                }
            });
            path = zkClient.create(path, null, Collections.singletonList(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE)), CreateMode.EPHEMERAL_SEQUENTIAL);
            registered = true;
        } catch (IOException e) {
            log.error("Exception occurred when try to connect to zookeeper " + zkConnectionString, e);
        } catch (InterruptedException e) {
            log.error("Exception occurred when try to create EPHEMERAL_SEQUENTIAL znode.", e);
        } catch (KeeperException e) {
            log.error("Exception occurred when try to create EPHEMERAL_SEQUENTIAL znode.", e);
        }
    }

    public boolean isLeader() {
        if (!registered) {
            register();
        }
        try {
            List<String> children = zkClient.getChildren(ephemeralZnodeParent, false);
            if (CollectionUtils.isEmpty(children)) {
                log.error("Get NO children of " + ephemeralZnodeParent);
                return false;
            }
            Collections.sort(children);
            String smallestChild = ephemeralZnodeParent + "/" + children.get(0);
            return path.equals(smallestChild);
        } catch (KeeperException e) {
            registered = false;
            log.error("Exception occurred when try to get children of " + ephemeralZnodeParent, e);
        } catch (InterruptedException e) {
            registered = false;
            log.error("Exception occurred when try to get children of " + ephemeralZnodeParent, e);
        }
        return false;
    }

    public void destroy() throws InterruptedException {
        zkClient.close();
    }


    public String getEphemeralZnodeNamePrefix() {
        return ephemeralZnodeNamePrefix;
    }

    public void setEphemeralZnodeNamePrefix(String ephemeralZnodeNamePrefix) {
        this.ephemeralZnodeNamePrefix = ephemeralZnodeNamePrefix;
    }

    public String getEphemeralZnodeParent() {
        return ephemeralZnodeParent;
    }

    public void setEphemeralZnodeParent(String ephemeralZnodeParent) {
        this.ephemeralZnodeParent = ephemeralZnodeParent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ZooKeeper getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZooKeeper zkClient) {
        this.zkClient = zkClient;
    }

    public String getZkConnectionString() {
        return zkConnectionString;
    }

    public void setZkConnectionString(String zkConnectionString) {
        this.zkConnectionString = zkConnectionString;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }
}
