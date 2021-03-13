package contact;

import java.util.List;

public class Contact {

	private Integer contactID = 0;
	private String contactName = "";
	private String emailAddress = "";
	private List<String> contactNumber = null;
	public Integer getContactID() {
		return contactID;
	}
	public void setContactID(Integer contactID) {
		this.contactID = contactID;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public List<String> getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(List<String> contactNumber) {
		this.contactNumber = contactNumber;
	}
	public Contact(Integer contactID, String contactName, String emailAddress, List<String> contactNumber) {
		super();
		this.contactID = contactID;
		this.contactName = contactName;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}
	
	public Contact(Integer contactID) {
		
		this.contactID = contactID;
		
	}
	
	public void display()
	{
		System.out.println("\nName : " + this.contactName + "\nEmail Address: " + this.emailAddress 
				+ "\nContact Number: " + this.contactNumber + "\n" +"Contact ID : "+this.contactID+"\n");
	}
	
	
}
