package org.moonlightcontroller.spammer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.moonlightcontroller.southbound.client.SingleInstanceConnection;

public class ObiMock {

	private static ObiMock instance;

	private long dpid;
	private int xid;
	private Server jetty;
	private SingleInstanceConnection client;

	public static ObiMock getInstance() {
		return instance;
	}
	
	public SingleInstanceConnection getClient() {
		return this.client;
	}
	
	public long getdpid() {
		return this.dpid;
	}
	
	public ObiMock(int dpid, String serverIp, int serverPort) {
		this.dpid = dpid;
		this.client = new SingleInstanceConnection(serverIp, serverPort);
		instance = this;
	}
	
	public int fetchAndIncxid(){
		int ret = this.xid;
		this.xid++;
		return ret;
	}

	public void start() throws Exception {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(3636);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", ObiMockApi.class.getCanonicalName());

		this.jetty = jettyServer;
		try {
			this.jetty.start();
 			// this.jetty.join();
		} catch(Exception e){
			this.jetty.destroy();
		}
	}
	
	public static void main(String[] args) {
		int obid = 22;
		
		int sleep = 0;
		if (args.length > 0){
		    try {
		    	sleep = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Argument" + args[0] + " must be a vaild port number.");
		        System.exit(1);
		    }
		}
		
		ObiMock obi = new ObiMock(obid, "127.0.0.1", 3637);
		try {
			obi.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ObiMockApi api = new ObiMockApi();
		api.sayhello();
		
		Spammer sp = new Spammer("127.0.0.1", 3637, sleep);
		sp.runSpammer();
		
		try {
			obi.jetty.join();	
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			obi.jetty.destroy();
		}
		
	}
}
