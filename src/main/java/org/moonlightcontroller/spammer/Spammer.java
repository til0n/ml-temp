package org.moonlightcontroller.spammer;

import java.util.ArrayList;
import java.util.List;

import org.moonlightcontroller.managers.models.messages.Alert;
import org.moonlightcontroller.managers.models.messages.AlertMessage;
import org.moonlightcontroller.managers.models.messages.IMessage;
import org.moonlightcontroller.southbound.client.SingleInstanceConnection;

public class Spammer {

	private int xid;
	private SingleInstanceConnection client;
	private int sleep;
	
	public Spammer(String serverIp, int serverPort, int amountInMinute) {
		this.client = new SingleInstanceConnection(serverIp, serverPort);
		this.sleep = (60*1000)/amountInMinute;
	}
	
	public void runSpammer() {
		while (true) {
			try {
				IMessage msg = this.createAlert();
				this.client.sendMessage(msg);
				Thread.sleep(this.sleep);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private IMessage createAlert() {
		int xid = this.fetchAndIncxid();
		List<AlertMessage> alerts = new ArrayList<>();
		alerts.add(new AlertMessage(1, System.nanoTime(), "Alert from mock OBI", 1, "packet", "AlertChecker.Alert"));
		Alert alertMessage = new Alert(xid, ObiMock.getInstance().getdpid(), alerts);
		return alertMessage;
	}
	
	private int fetchAndIncxid(){
		int ret = this.xid;
		this.xid++;
		return ret;
	}
}
