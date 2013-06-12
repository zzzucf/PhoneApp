package com.example.phoneapp;

import java.util.ArrayList;

import Classes.ContactAttribute;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditContactActivity extends Activity {

	private ArrayList<ContactAttribute> userContacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_edit_contact, menu);
		return true;
	}
	
	public void ImportContacts()
	{
		
	}
	
	public void ExportContacts()
	{
		
	}

}
