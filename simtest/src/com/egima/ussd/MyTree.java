package com.egima.ussd;

import java.util.HashMap;
import java.util.Map;

import com.egimaben.ussd.UssdNode;
import com.egimaben.ussd.UssdPrompt;
import com.egimaben.ussd.UssdTree;

/**
 * 
 * @author Egima
 * 
 */
public class MyTree extends UssdTree {

	public MyTree(String treeHeader, String address) {
		super(treeHeader, address);
		initTree();
		setReady(true);
	}

	private void initTree() {
		/***********************************************************************
		 * add first level children falling directly under root menu, give them
		 * suitable id's which must be unique across the whole tree
		 ************************************************************************/
		addNode(new UssdNode("Send Money", "sendmoney", "root"));
		addNode(new UssdNode("Airtime/Data", "airtimedata", "root"));
		addNode(new UssdNode("Widthdraw Cash", "withdrawcash", "root"));
		addNode(new UssdNode("Pay Bill", "paybill", "root"));
		addNode(new UssdNode("Buy Goods", "buygoods", "root"));
		addNode(new UssdNode("Check Balance", "checkbalance", "root"));
		addNode(new UssdNode("Financial Services", "financialservices", "root"));
		addNode(new UssdNode("My Account", "myaccount", "root"));
		addNode(new UssdNode("Messages", "messages", "root"));

		/***********************************************************************
		 * add the children of each menu item in similar manner e.g for paybill
		 * item
		 * ********************************************************************/
		addNode(new UssdNode("UMEME touchpay", "umeme", "paybill"));
		addNode(new UssdNode("NWSC eWater", "nwsc", "paybill"));
		addNode(new UssdNode("Pay TV", "paytv", "paybill"));
		/***********************************************************************
		 * at this rate, you can pick on and supply menus to the rest, lets look
		 * at a UssdPrompt Assume on selecting UMEME touchpay option, you are
		 * asked for account number, amount to pay and mobile money pin, each of
		 * which are prompts
		 */
		addNode(new UssdPrompt("Enter UMEME account No", "umemeaccno", "umeme") {

			@Override
			public String getValidationError() {
				/*
				 * In case the account number is not valid
				 */
				return "Unknown account number, please check your entry";
			}

			@Override
			public UssdNode[] getDynamicNodes(Map<String, Object> arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> updateDataState(
					Map<String, Object> arg0, Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean validate(Object arg0, Map<String, Object> arg1) {
				/*
				 * probably here you'd check if the input account number is in
				 * your DB but lets assume there is only 1 client and his
				 * account number is 1100
				 */
				String input = (String) arg0;
				return "1100".equals(input);
			}
		});
		/******************************************************************
		 * after the first prompt passes, the next prompt is called, so a prompt
		 * must have only 1 child i.e the next prompt
		 */
		addNode(new UssdPrompt("How much do you want to pay", "amount",
				"umemeaccno") {

			@Override
			public String getValidationError() {
				return "Invalid cash amount";
			}

			@Override
			public UssdNode[] getDynamicNodes(Map<String, Object> arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> updateDataState(
					Map<String, Object> arg0, Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean validate(Object arg0, Map<String, Object> arg1) {
				/*
				 * since this is a cash amount, we shall just ensure it's a
				 * valid double figure greater than 0
				 */
				double amount = 0;
				try {
					amount = Double.parseDouble((String) arg0);
				} catch (Exception e) {
					return false;
				}
				return amount > 0;
			}
		});
		/******************************************************************
		 * finally we add another child to amount prompt to ask for the pin,
		 * since this is the last prompt(has no children), we shall override the
		 * processNodeEndEvent method to perform our session end processing
		 */
		addNode(new UssdPrompt("Enter Mobile money pin", "pin", "amount") {

			@Override
			public String getValidationError() {
				return "Invalid pin";
			}

			@Override
			public String processNodeEndEvent(HashMap<String, Object> userData) {
				/*
				 * let's retrieve all the data we need now by node name
				 */
				// umemeaccno
				String accno = (String) userData.get("umemeaccno");
				// amount
				double amt = Double
						.parseDouble((String) userData.get("amount"));
				// pin
				String pin = (String) userData.get("pin");
				/*
				 * assume the initial balance was 300,000/= and we are going to
				 * deduct th input amount
				 */
				double balance = 300000 - amt;

				/*
				 * just for clarity and understanding purposes, we shall send
				 * this data to display
				 */
				StringBuilder data = new StringBuilder();
				data.append("Dear customer<br>");
				data.append("ACC NO:" + accno + "<br>");
				data.append("PIN:" + pin + "<br>");
				data.append("AMT:" + amt + "<br>");
				data.append("BAL:" + balance);
				return data.toString();
			}

			@Override
			public UssdNode[] getDynamicNodes(Map<String, Object> arg0,
					Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> updateDataState(
					Map<String, Object> arg0, Object arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean validate(Object arg0, Map<String, Object> arg1) {
				/*
				 * lets assume the pin is 4649, otherwise you'd check from the
				 * DB
				 */

				return "4649".equals(arg0);
			}

		});
	}

}
