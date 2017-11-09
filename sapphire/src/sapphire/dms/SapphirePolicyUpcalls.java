package sapphire.dms;

import java.io.Serializable;
import java.util.ArrayList;

import sapphire.dms.SapphirePolicy.SapphireGroupPolicy;
import sapphire.dms.SapphirePolicy.SapphireServerPolicy;

public interface SapphirePolicyUpcalls {
	public interface  SapphireClientPolicyUpcalls extends Serializable {
		public void onCreate(SapphireGroupPolicy group);
		public void setServer(SapphireServerPolicy server);
		public SapphireServerPolicy getServer();
		public SapphireGroupPolicy getGroup();
		public Object onRPC(String method, ArrayList<Object> params) throws Exception;
	}
	
	public interface SapphireServerPolicyUpcalls extends Serializable {
		public void onCreate(SapphireGroupPolicy group);
		public SapphireGroupPolicy getGroup();
		public Object onRPC(String method, ArrayList<Object> params) throws Exception;
		public void onMembershipChange();
	}
	
	public interface SapphireGroupPolicyUpcalls extends Serializable {
		public void onCreate(SapphireServerPolicy server);
		public void addServer(SapphireServerPolicy server);
		public ArrayList<SapphireServerPolicy> getServers();
		public void onFailure(SapphireServerPolicy server);
		public SapphireServerPolicy onRefRequest();
	}
}
