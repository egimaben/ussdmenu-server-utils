/*
 *   (C) Copyright 2010-2011 hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package hms.sdp.ussd.client;

import hms.sdp.ussd.MchoiceUssdMessage;
import hms.sdp.ussd.MchoiceUssdTerminateMessage;
import hms.sdp.ussd.impl.UssdAoRequestMessage;
import hms.sdp.ussd.impl.UssdAtRequestMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.neskom.gsb.ussd.dto.USSDRequest;
import com.neskom.gsb.ussd.dto.USSDResponse;

/**
 * $LastChangedDate$ $LastChangedBy$ $LastChangedRevision$
 */
public abstract class MchoiceUssdReceiver extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(MchoiceUssdReceiver.class);
	private XmlMapper mapper;

	// private MapperFactory mapperFactory;
	// private MapperFacade mapperFacade;

	/**
	 * this life-cycle method is invoked when this servlet is first accessed by
	 * the client
	 */
	public void init(ServletConfig config) {
		mapper = new XmlMapper();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String ussdMessageType = req
				.getHeader(MchoiceUssdMessage.USSD_MESSAGE_TYPE);
		final Gson gson = new Gson();
		final ServletInputStream inputStream = req.getInputStream();
		
		final USSDRequest request = mapper.readValue(inputStream,
				USSDRequest.class);
		System.out.println("request=" + request);
		UssdAtRequestMessage atRequestMessage = new UssdAtRequestMessage();
		atRequestMessage.setAddress(request.getMSISDN());
		atRequestMessage.setConversationId(request.getTransactionId());
		atRequestMessage.setMessage(request.getUSSDRequestString());
		atRequestMessage.setShortcode(request.getUSSDServiceCode());
		atRequestMessage.setSessionTermination(false);
		UssdAoRequestMessage response = onMessage(atRequestMessage);
		USSDResponse rawResp = new USSDResponse();
		rawResp.setTransactionId(response.getConversationId());
		rawResp.setTransactionTime(request.getTransactionTime());
		rawResp.setUSSDAction(response.getSessionTermination() ? "end"
				: "request");
		rawResp.setUSSDResponseString(response.getMessage());
		String fineResp = mapper.writeValueAsString(rawResp);
		System.out.println("response=" + fineResp);
		resp.setContentType("text/xml;charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.append("<?xml version=\"1.0\"?>");
		writer.append(fineResp);
		writer.flush();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final ServletInputStream inputStream = req.getInputStream();
		final USSDRequest request = mapper.readValue(inputStream,
				USSDRequest.class);
		UssdAtRequestMessage atRequestMessage = new UssdAtRequestMessage();
		atRequestMessage.setAddress(request.getMSISDN());
		atRequestMessage.setConversationId(request.getTransactionId());
		atRequestMessage.setMessage(request.getUSSDRequestString());
		atRequestMessage.setShortcode(request.getUSSDServiceCode());
		atRequestMessage.setSessionTermination(false);
		UssdAoRequestMessage response = onMessage(atRequestMessage);
		USSDResponse rawResp = new USSDResponse();
		rawResp.setTransactionId(response.getConversationId());
		rawResp.setTransactionTime(request.getTransactionTime());
		rawResp.setUSSDAction(response.getSessionTermination() ? "end"
				: "request");
		rawResp.setUSSDResponseString(response.getMessage());
		String fineResp = mapper.writeValueAsString(rawResp);
		resp.setContentType("text/xml;charset=UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.append("<?xml version=\"1.0\"?>");
		writer.append(fineResp);
		writer.flush();
	}

	public static String inputStreamToString(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			sb.append("Null response received");
		} finally {
			if (br != null) {
				try {
					// is.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	private String readBody(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));

		StringBuilder line = new StringBuilder();
		String li;
		while ((li = in.readLine()) != null) {
			line.append(li);
		}
		return line.toString();
	}

	public abstract UssdAoRequestMessage onMessage(
			MchoiceUssdMessage atRequestMessage);

	public abstract void onSessionTerminate(MchoiceUssdTerminateMessage message);
}
