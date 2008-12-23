package uk.ac.lancs.e_science.profile2.tool.pages;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import uk.ac.lancs.e_science.profile2.hbm.Friend;
import uk.ac.lancs.e_science.profile2.tool.pages.windows.RemoveFriend;


public class MyFriends extends BasePage {

	private transient Logger log = Logger.getLogger(MyFriends.class);
	private static final String UNAVAILABLE_IMAGE = "images/no_image.gif";
	
	public MyFriends() {
		
		if(log.isDebugEnabled()) log.debug("MyFriends()");
		
		//add the feedback panel for any error messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
		add(feedbackPanel);
		feedbackPanel.setVisible(false); //hide by default

		//get current user
		final String userId = sakaiProxy.getCurrentUserId();

		
	
		
		WebMarkupContainer friendRequestsContainer = new WebMarkupContainer("friendRequests-container");
		friendRequestsContainer.setOutputMarkupId(true);
		
		//heading
		friendRequestsContainer.add(new Label("friendRequests-heading", new ResourceModel("heading.friend.requests")));
		
		//get friend requests for user
		List<Friend> requests = new ArrayList<Friend>(profile.getFriendRequestsForUser(userId));
		
		
		ListView requestsListView = new ListView("friendRequests-list", requests) {
		    protected void populateItem(ListItem item) {
		        
		    	//get a friend object for each user, containing items that we need to list here
		    	//we also need their privacy settings
		    	
		    	//in the friend object add in the photo stream as a param
		    	
		    	
		    	//get Friend object
		    	Friend friend = (Friend)item.getModelObject();
		    	
		    	//setup basic values
		    	String displayName = sakaiProxy.getUserDisplayName(friend.getUserUuid());
		    	final byte[] photo = friend.getPhoto();
		    			    			    	
		    	//name
		    	Label nameLabel = new Label("friendRequest-name", displayName);
		    	item.add(nameLabel);
		    	
		    	
		    	//photo
		    	if(photo != null && photo.length > 0){
		    		
					BufferedDynamicImageResource photoResource = new BufferedDynamicImageResource(){
						protected byte[] getImageData() {
							return photo;
						}
					};
				
					item.add(new Image("friendRequest-photo",photoResource));
				} else {
					item.add(new ContextImage("friendRequest-photo",new Model(UNAVAILABLE_IMAGE)));
				}
		
		    	
		    	
		    	//action - confirm friend
		    	AjaxLink confirmLink = new AjaxLink("friendRequest-confirmLink") {
		    		public void onClick(AjaxRequestTarget target) {
		    			
	    			}
	    		};
	    		confirmLink.add(new Label("friendRequest-confirm",new ResourceModel("link.friend.request.confirm")));
	    		item.add(confirmLink);
	    		
	    		
	    		
		    }
		};
		friendRequestsContainer.add(requestsListView);
		
		//add friend container
		add(friendRequestsContainer);
		
		//if no friend requests, hide
		if(requests.isEmpty()) {
			friendRequestsContainer.setVisible(false);
		}
		
		
		
		
		
		
		WebMarkupContainer friendsContainer = new WebMarkupContainer("friends-container");
		friendsContainer.setOutputMarkupId(true);
		
		//heading
		friendsContainer.add(new Label("friends-heading", new ResourceModel("heading.friends")));
		
		//no friends message
		Label noFriends = new Label("friends-none");
		noFriends.setOutputMarkupId(true);
		add(noFriends);
		
		//remove friend modal window
		final ModalWindow removeFriendWindow = new ModalWindow("friend-removeWindow");
		friendsContainer.add(removeFriendWindow);
		
		//the setup for this modal window is done in the AjaxLink below. see there for info.
		
		removeFriendWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
            	System.out.println("window closed 1");
            	return true;
            }
        });

		removeFriendWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target){
            	System.out.println("window closed 2");
            }
        });
		
		
		//get friends for user
		List<Friend> friends = new ArrayList<Friend>(profile.getFriendsForUser(userId, 0));
		
		
		ListView friendsListView = new ListView("friends-list", friends) {
		    protected void populateItem(ListItem item) {
		        
		    	//get a friend object for each user, containing items that we need to list here
		    	//we also need their privacy settings
		    	
		    	//in the friend object add in the photo stream as a param
		    	
		    	
		    	//get Friend object
		    	final Friend friend = (Friend)item.getModelObject();
		    	
		    	//setup basic values
		    	String displayName = sakaiProxy.getUserDisplayName(friend.getUserUuid());
		    	friend.setDisplayName(displayName);
		    	String statusMessage = friend.getStatusMessage();
		    	Date statusDate = friend.getStatusDate();
		    	boolean confirmed = friend.isConfirmed();
		    	final byte[] photo = friend.getPhoto();
		    			    			    	
		    	//name
		    	Label nameLabel = new Label("friend-name", displayName);
		    	item.add(nameLabel);
		    	
		    	//status - no default value, set it later
		    	Label statusMessageLabel = new Label("friend-statusMessage");
		    	item.add(statusMessageLabel);
		    	
		    	//statusDate - no default value, set it later
		    	Label statusDateLabel = new Label("friend-statusDate");
		    	item.add(statusDateLabel);
		    	
		    	//friend requested message
		    	Label friendRequested = new Label("friend-confirmation");
		    	item.add(friendRequested);
		    	
		    	//photo
		    	if(photo != null && photo.length > 0){
		    		
					BufferedDynamicImageResource photoResource = new BufferedDynamicImageResource(){
						protected byte[] getImageData() {
							return photo;
						}
					};
				
					item.add(new Image("friend-photo",photoResource));
				} else {
					item.add(new ContextImage("friend-photo",new Model(UNAVAILABLE_IMAGE)));
				}
		    	

		    	
		    	//action - remove friend
		    	AjaxLink removeLink = new AjaxLink("friend-removeLink") {
		    		public void onClick(AjaxRequestTarget target) {
		    			
		    			//setup content panel for removeFriendWindow. This is a custom panel with setters for the data to be used inside
		    			//we set the data into this Panel when the ajax button is clicked.
		    			
		    			RemoveFriend removeFriend = new RemoveFriend(removeFriendWindow.getContentId(), userId, friend);
		    			removeFriendWindow.setContent(removeFriend);
		    			//removeFriendWindow.setTitle(new ResourceModel("window.title.friend.remove"));
		    			removeFriendWindow.setHeightUnit("50");
		    			removeFriendWindow.setCookieName("profileModalWindow");
		    			
		    			removeFriendWindow.show(target);
	    			}
	    		};
	    		
	    		Label removeLinkLabel = new Label("friend-remove");
	    		removeLink.add(removeLinkLabel);
	    		item.add(removeLink);
	    		
	    		
	    		
	    		//now set the models/hide on the above objects depending on their content
		    	if(statusMessage == null) {
		    		statusMessageLabel.setVisible(false);
		    	} else 	{
		    		statusMessageLabel.setModel(new Model(statusMessage));
		    	}
		    	
		    	if(statusDate == null) {
		    		statusDateLabel.setVisible(false);
		    	} else 	{
		    		statusDateLabel.setModel(new Model(profile.convertDateForStatus(statusDate)));
		    	}
		    	
		    	if(confirmed) {
		    		friendRequested.setVisible(false);
		    		removeLinkLabel.setModel(new ResourceModel("link.friend.remove"));
		    	} else 	{
		    		friendRequested.setModel(new ResourceModel("text.friend.requested"));
		    		removeLinkLabel.setModel(new ResourceModel("link.friend.request.cancel"));
		    	}
		    	
	    		
	    		
		    }
		};
		friendsContainer.add(friendsListView);
		
		//add friend container
		add(friendsContainer);
		
		//if no friends, show message and hide container
		if(friends.isEmpty()) {
			noFriends.setModel(new ResourceModel("text.no.friends"));
			friendsContainer.setVisible(false);
		} else {
			noFriends.setVisible(false);
		}
	
		
	}
}



