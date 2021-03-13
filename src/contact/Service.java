package contact;

import java.sql.Statement;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

public class Service {
static Connection conn = null;

	public static Connection databaseConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String url = "jdbc:oracle:thin:@localhost:1521:XE";
		String username = "SYSTEM";
		String password = "sys";
		
		
		try {
			conn = DriverManager.getConnection(url, username, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return conn;
	}
	
	public void addContact(Contact contact, List<Contact> contacts) {
		contacts.add(contact);
		System.out.println("new contact added..!");
	}
	
	
	public void removeContact(Contact contact, List<Contact> contacts) throws NotFoundException 
	{
		Contact con = null;
		for (Contact c : contacts)
			if (c.getContactID() == contact.getContactID())
				con = c;
		if (con == null) {
			throw new NotFoundException("Contact not in the provided list");
			
		}
		else {
			contacts.remove(con);
			System.out.println("Contact Removed");
		}
	}
	
	public Contact searchContactByName(String name, List<Contact> contacts) throws NotFoundException{
		for (Contact c : contacts) 
		{
			if (c.getContactName().equals(name)) 
			{
				return c;
			}
		}
		throw new NotFoundException("Contact is not in the list");
	}
	
	
	public List<Contact> searchContactByNumber(String number, List<Contact> contacts) throws NotFoundException {
		List<Contact> contactList = new ArrayList<Contact>();
		for (Contact c : contacts) {
			for (String contactNumber : c.getContactNumber())
				if (contactNumber.contains(number)) {
					contactList.add(c);
					break;
				}
		}
		if (contactList.size() == 0)
			throw new NotFoundException("Contact is not in the list");
		
		return contactList;
	}
	
	
	public void addContactNumber(int contactId, String contactNo, List<Contact> contacts) {
		List<String> contactNumber = new ArrayList<String>();
		for(String s : contactNo.split(","))
		{	
			contactNumber.add(s);
		}	
		for (Contact c : contacts) 
		{
			if (c.getContactID() == contactId) 
			{
				c.setContactNumber(contactNumber);
			}
		}
	}
	
	
	public void sortByName(List<Contact> contacts) {
		Comparator<Contact> cm = Comparator.comparing(Contact::getContactName);
		Collections.sort(contacts, cm);
		System.out.println("Contact list sorted by name");
		
	}
		
	public void readContacts(List<Contact> contacts, String fileName) {
		File file = new File(fileName);
		Scanner read;
		try {
			read = new Scanner(file);
			String [] contactInformation = null;
			List<String> contactNumber = new ArrayList<String>();
			
			while (read.hasNextLine()) {
				Integer contactid = 0;
				String contactName = "", emailAddress = "", contactNumberString = "";
				contactInformation = read.nextLine().split(",");
				for (int i = 0; i < contactInformation.length; i++) {
					if (i == 0)
						contactid = Integer.parseInt(contactInformation[i]);
					else if (i == 1)
						contactName = contactInformation[1];
					else if (i == 2)
						emailAddress = contactInformation[2];
					else {
						contactNumberString += contactInformation[i] + ",";
						for (String s : contactNumberString.split(","))
							contactNumber.add(s);
					}
				}
				Contact c = new Contact(contactid, contactName, emailAddress, contactNumber);
				contacts.add(c);
			}
			read.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		System.out.println("Data inserted...!");
	}
	
	
	public void serializeContactDetails(List<Contact> contacts , String file_name) {
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		
		try {
			fout = new FileOutputStream(file_name, true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(contacts);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println("Contact details serialized");
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Contact> deserializeContact(String fileName) {
		ObjectInputStream ois = null;
		List<Contact> contacts = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ois = new ObjectInputStream(fin);
			contacts = (List<Contact>) ois.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Contact details deserialized");
		return contacts;
	}
	
	
public Set<Contact> populateContactFromDb(){
		
		Set<Contact> contactSet = new HashSet<Contact>();
		conn = databaseConnection();
		try {
			String sql = "SELECT * FROM contact_tbl";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				List<String> contactNumber = new ArrayList<String>();
				Integer contactID = 0;
				String contactName = "", emailAddress = "", contactNumberString = "";
				if (rs.getInt("CONTACTID") != 0)
					contactID = rs.getInt("CONTACTID");
				if (rs.getString("CONTACTNAME") != null)
					contactName = rs.getString("CONTACTNAME");
				if (rs.getString("CONTACTEMAIL") != null)
					emailAddress = rs.getString("CONTACTEMAIL");
				if (rs.getString("CONTACTLIST") != null)
					contactNumberString = rs.getString("CONTACTLIST");
				for (String s : contactNumberString.split(","))
					contactNumber.add(s);
				Contact c = new Contact(contactID, contactName, emailAddress, contactNumber);
				contactSet.add(c);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactSet;
	}

	
	
	public Boolean addContacts(List<Contact> existingContact,Set<Contact> newContacts) {
		try {
			for (Contact c : newContacts)
				existingContact.add(c);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	
	public static void main(String[] args) {
		Scanner read = new Scanner(System.in);
		List<Contact> contacts = new ArrayList<Contact>();
		Service cs = new Service();
		int choice;
		Integer contactID;
		String contactName, emailAddress, contactNumberString;
		List<String> contactNumber = null;
		
		do {
			System.out.println("1 .Display contacts\n"
					+ "2. Add contact\n"
					+ "3. Remove contact\n"
					+ "4. Search by contact name\n"
					+ "5. Search by contact number\n"
					+ "6. Set contact number\n"
					+ "7. Sort contact by name\n"
					+ "8. Add contacts from file\n"
					+ "9. Serialize contact details\n"
					+ "10.Deserialize contact details\n"
					+ "11. Populate from DB\n"
					+ "12. Add new contacts to existing ones\nPress -1 to quit\n");
			System.out.print("Enter your choice: ");
			choice = read.nextInt();
			switch(choice) {
			
			case 1:
				System.out.println("following are the contacts");
				for (Contact c : contacts)
					c.display();
				break;
			
			case 2:
				System.out.print("Enter contact ID: ");
				contactID = read.nextInt();
				read.nextLine();
				System.out.print("Enter contact name: ");
				contactName = read.nextLine();
				System.out.print("Enter contact email address: ");
				emailAddress = read.nextLine();
				System.out.print("Enter contact number: ");
				contactNumberString = read.nextLine();
				contactNumber = new ArrayList<String>();
				for (String s : contactNumberString.split(","))
						contactNumber.add(s);
				cs.addContact(new Contact(contactID, contactName, emailAddress, contactNumber), contacts);
				break;
			
			case 3:
				System.out.print("\nEnter the contact ID: ");
				contactID = read.nextInt();
				read.nextLine();

				try {
					cs.removeContact(new Contact(contactID), contacts);
				} catch (NotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 4:
				System.out.print("\nEnter the contact name: ");
				contactName = read.next();
				try {
					Contact c = cs.searchContactByName(contactName, contacts);
					c.display();
				} catch (NotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 5:
				System.out.print("\nEnter the contact number: ");
				contactNumberString = read.next();
				try {
					List<Contact> c = cs.searchContactByNumber(contactNumberString, contacts);
					for (Contact ct : c)
						ct.display();
				} catch (NotFoundException e) {
					System.out.println(e);
				}
				break;
				
			case 6:
				System.out.print("\nEnter contact ID: ");
				contactID = read.nextInt();
				read.nextLine();
				System.out.print("\nEnter contact number: ");
				contactNumberString = read.nextLine();
				cs.addContactNumber(contactID, contactNumberString, contacts);
				break;
				
			case 7:
				cs.sortByName(contacts);
				break;
				
			case 8:
				try {
					
					cs.readContacts(contacts, "C:\\\\Users\\\\Nishi\\\\eclipse-workspace\\\\Ass_10\\\\Contact.txt");
					break;
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
				
			
			case 9:
				
				try 
				{	
					 cs.serializeContactDetails(contacts, "C:\\\\Users\\\\Nishi\\\\eclipse-workspace\\\\Ass_10\\\\output.ser");
					 break;
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
				
				
			case 10:
				
				try 
				{	
					List<Contact> c = cs.deserializeContact("C:\\\\Users\\\\Nishi\\\\eclipse-workspace\\\\Ass_10\\\\output.ser");
					for (Contact ct : c)
						ct.display();
					break;
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
				
				
				
			case 11:
				
				try 
				{	
					Set<Contact> contactSet  = new HashSet<Contact>();
					contactSet = cs.populateContactFromDb();
					Iterator itr = contactSet.iterator();
					while (itr.hasNext()) {
						Contact ct = (Contact) itr.next();
						ct.display();
					}
					break;
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
				
				
				
			case 12:
				
				try 
				{
					Set<Contact> contactSet  = new HashSet<Contact>();
					contactSet = cs.populateContactFromDb();
					if (cs.addContacts(contacts, contactSet)) {
						System.out.println("New contacts added..");
					}
					else {
						System.out.println("\nError please try again");
					}
					
					break;
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				
				
				
			default :
				System.out.println("enter a valid choice:");
				
					
			}
			

		} while(choice != -1);
		read.close();
	}

}
