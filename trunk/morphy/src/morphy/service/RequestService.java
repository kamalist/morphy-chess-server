/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008-2010  http://code.google.com/p/morphy-chess-server/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package morphy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import morphy.game.request.Request;
import morphy.user.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestService implements Service {
	protected static Log LOG = LogFactory.getLog(RequestService.class);
	private static RequestService singletonInstance = new RequestService();
	
	public static RequestService getInstance() {
		return singletonInstance;
	}
	
	private HashMap<UserSession,List<Request>> fromMap;
	private HashMap<UserSession,List<Request>> toMap;
	private List<Integer> availableRequestNumbers; 

	public RequestService() {
		fromMap = new HashMap<UserSession,List<Request>>();
		toMap = new HashMap<UserSession,List<Request>>();
		availableRequestNumbers = new ArrayList<Integer>(10);
		for(int i=1;i<10;i++) {
			availableRequestNumbers.add(new Integer(i));
		}
	}
	
	private int getNextAvailableNumber() {
		if (availableRequestNumbers.size() == 0) {
			LOG.info("There are no more available request numbers available, they need to be refilled. (TODO).");
		}
		return availableRequestNumbers.get(0);
	}
	
	public void addRequest(UserSession from,UserSession to,Request req) {
		req.setRequestNumber(getNextAvailableNumber());
		availableRequestNumbers.remove(0);
		if (!fromMap.containsKey(from)) {
			fromMap.put(from,new ArrayList<Request>(10));
		}
		fromMap.get(from).add(req);
		
		if (!toMap.containsKey(to)) {
			toMap.put(to,new ArrayList<Request>(10));
		}
		toMap.get(to).add(req);
	}
	
	public List<Request> getRequestsFrom(UserSession userSession) {
		if (fromMap.containsKey(userSession)) {
			return fromMap.get(userSession);
		}
		return null;
	}
	
	public List<Request> getRequestsTo(UserSession userSession) {
		if (toMap.containsKey(userSession)) {
			return toMap.get(userSession);
		}
		return null;
	}
	
	public List<Request> findAllFromRequestsByType(UserSession userSession,Class<? extends Request> type) {
		if (!fromMap.containsKey(userSession)) return null;
		
		List<Request> rList = fromMap.get(userSession);
		List<Request> copy = new ArrayList<Request>();
		for(int i=0;i<rList.size();i++) {
			Request r = rList.get(i); 
			if (r.getClass() == type) { copy.add(r); }
		}
		return copy;
	}
	
	public List<Request> findAllToRequestsByType(UserSession userSession,Class<? extends Request> type) {
		if (!toMap.containsKey(userSession)) return null;
		
		List<Request> rList = toMap.get(userSession);
		List<Request> copy = new ArrayList<Request>();
		for(int i=0;i<rList.size();i++) {
			Request r = rList.get(i); 
			if (r.getClass() == type) { copy.add(r); }
		}
		return copy;
	}
	
	/** This method removes all requests of type "type" from outgoing offers. */
	public void removeRequestsFrom(UserSession userSession,Class<? extends Request> type) {
		if (!fromMap.containsKey(userSession)) return;
		
		List<Request> rList = fromMap.get(userSession);
		for(int i=0;i<rList.size();i++) {
			Request r = rList.get(i); 
			if (r.getClass() == type) {
				 rList.remove(i--); 
				if (!availableRequestNumbers.contains(r.getRequestNumber())) {
					availableRequestNumbers.add(0,r.getRequestNumber());
				}
			}
		}
	}
	
	public void removeRequestsTo(UserSession userSession,Class<? extends Request> type) {
		if (!toMap.containsKey(userSession)) return;
		
		List<Request> rList = toMap.get(userSession);
		for(int i=0;i<rList.size();i++) {
			Request r = rList.get(i); 
			if (r.getClass() == type) {
				 rList.remove(i--); 
				if (!availableRequestNumbers.contains(r.getRequestNumber())) {
					availableRequestNumbers.add(0,r.getRequestNumber());
				}
			}
		}
	}
	
	public void removeRequestFrom(UserSession userSession,Request instance) {
		if (!fromMap.containsKey(userSession)) return;
		
		List<Request> rList = fromMap.get(userSession);
		rList.remove(instance);
	}
	
	public void removeRequestTo(UserSession userSession,Request instance) {
		if (!toMap.containsKey(userSession)) return;
		
		List<Request> rList = toMap.get(userSession);
		rList.remove(instance);
	}
	
	public void removeAllRequestsTo(UserSession userSession) {
		if (!toMap.containsKey(userSession)) return;
		
		List<Request> rList = toMap.get(userSession);
		rList.clear();
	}
	
	public void recycleRequestNumber(int num) {
		if (!availableRequestNumbers.contains(num)) {
			availableRequestNumbers.add(new Integer(num));
		}
	}
	
	public void dispose() {
		if (LOG.isInfoEnabled())
			LOG.info("RequestService Disposed.");
	}

}
