package com.egima.ussd;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egimaben.ussd.UssdReceiver;
import com.egimaben.ussd.UssdTree;
import com.egimaben.ussd.dto.USSDRequest;
import com.egimaben.ussd.dto.USSDResponse;

/**
 * 
 * @author Egima
 * 
 */
public class MyUssdReceiver extends UssdReceiver {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyUssdReceiver.class);

	private void l(String s) {
		LOGGER.info(s);
	}

	@Override
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		l("received post request:");
		USSDRequest req = new USSDRequest();
		String msisdn = arg0.getParameter("msisdn");
		String message = arg0.getParameter("message");
		req.setMSISDN(msisdn);
		req.setTransactionId("asdfadsfa");
		req.setTransactionTime("asdfasdf");
		req.setUSSDRequestString(message);
		req.setUSSDServiceCode("*185#");
		USSDResponse resp = onLiveMessage(req);
		arg1.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter writer = arg1.getWriter();
		String jsonString = "{data:\"" + resp.getUSSDResponseString() + "\"}";
		writer.append(resp.getUSSDResponseString());
		writer.flush();

	}

	@Override
	public String getAppShortCode() {
		// can be any short code of your choice, we are just simulating
		return "*185#";
	}

	@Override
	public int getBufferLimit() {
		return 5;
	}

	@Override
	public String getUssdAppId() {
		return "appid";
	}

	@Override
	public String getUssdAppPassword() {
		return "password";
	}

	@Override
	public String getUssdClientUrl() {
		return "http://127.0.0.1:8000/ussd/";
	}

	@Override
	public UssdTree getUssdTree(String address) {
		return new MyTree("Airtel Money", address);
	}

	@Override
	public String getDelim() {
		// TODO Auto-generated method stub
		return ",";
	}

}
