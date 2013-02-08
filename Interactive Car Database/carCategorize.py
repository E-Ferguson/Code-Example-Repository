
from Tkinter import *
import ttk
import tkMessageBox

import psycopg2
import getpass
import sys

import csv


sys.path.append("/usr/lib64/python")
sys.path.append("/usr/lib64/python2.4") 
sys.path.append("/usr/lib64/python2.5")
sys.path.append("/usr/lib64/python2.6")
sys.path.append("/usr/lib64/python2.7")
sys.path.append("/usr/lib64/python3.1")




class carCategorize(Frame):
	def __init__(self, parent, mainMenu):
		
		#establishing the connection
		try:
			connection = psycopg2.connect( database = "erfergus_cars", user = "erfergus", password = "bpedatabase")
			print "#connection successful#"

		except StandardError, e:
			print "!CONNECTION FAILED!"
			exit

		

		self.curs = connection.cursor()
		print "Welcome to the ?   This program provides access to a comprehensive "
		
		BODY_TYPES = [
		"Leave blank",
		    "Sedan",
		    "Sports Car",
		    "SUV",
		    "Wagon",
		    "Minivan"
		]
		
		PRICING_CATEGORIES = [
		"Leave blank",
		    "economy",
		    "Mid-level",
		    "Entry Luxury",
		    "Mid-level Luxury",
		    "High-level Luxury",
		    "Ultra luxury"
		]
		
		MAKES = ["Leave blank"]
		self.curs.execute("SELECT DISTINCT make FROM car;")
		for line in self.curs.fetchall():
			MAKES.append(line[0])
		
		FILTER_CATEGORY = [
		    "Highest",
		    "Lowest"
		]
		
		SEARCH_CATEGORIES = [
		    "Cargo room",
		    "Horsepower",
		    "Service cost",
		    "Insurance cost",
		    "City MPG",
		    "Highway MPG",
		    "MSRP"
		]		
		
		#self.root = Tk()

		#hookup to main menu#
		Frame.__init__(self, parent)
		
		self.root = parent
		self.mainMenu = mainMenu
		
		self.root.title ( "Find car categories" )
		self.grid();
		#-------------------#
		
		
		
		

		Label(self.root, text = "Number of results:").grid(row = 0,columnspan=2)#sticky = E)#, sticky = W+E+N+S)
		
		self.resultsVariable = StringVar()
		self.results = Entry(self.root, width=12, textvariable = self.resultsVariable)
		self.results.grid(row = 0, column = 1,columnspan=2)# sticky = W)#, padx=200)
		
		Label(self.root, text = "Body Type:").grid(row = 1, sticky = W)
		Label(self.root, text = "Price Category:").grid(row = 2, sticky = W)
		Label(self.root, text = "Make:").grid(row = 3, sticky = W)

		#creat Tk variables
		self.bt_variable = StringVar(self.root)
		self.bt_variable.set(BODY_TYPES[0]) # default value

		self.pricing_cat_variable = StringVar(self.root)
		self.pricing_cat_variable.set(PRICING_CATEGORIES[0])
		
		self.make_variable = StringVar(self.root)
		self.make_variable.set(MAKES[0])
		
		self.filter_type_variable = StringVar(self.root)
		self.filter_type_variable.set(FILTER_CATEGORY[0])
		
		#making the option menus
		self.body_type = OptionMenu(self.root, self.bt_variable, *BODY_TYPES)
		self.pricing_category = OptionMenu(self.root, self.pricing_cat_variable, *PRICING_CATEGORIES)
		self.make = OptionMenu(self.root, self.make_variable, *MAKES)
		
		
		#placing the option menus on the grid
		self.body_type.grid(row = 1, column = 1, sticky = W)
		self.pricing_category.grid(row = 2, column = 1, sticky = W)
		self.make.grid(row = 3, column = 1, sticky = W)

		Label(self.root, text = "Filter by:").grid(row = 6, sticky = W)
		self.filterType = OptionMenu(self.root, self.filter_type_variable, *FILTER_CATEGORY)
		self.filterType.grid(row = 6, column = 1, sticky = W)
		
		Label(self.root, text = "-"*90).grid(row = 5, column = 0, columnspan = 3, sticky = W)
		Label(self.root, text = "-"*90).grid(row = 7, column = 0, columnspan = 3, sticky = W)
		Label(self.root, text = "-"*90).grid(row = 9, column = 0, columnspan = 3, sticky = W)
		
		self.sc_variable = StringVar(self.root)
		self.sc_variable.set(SEARCH_CATEGORIES[0])
		
		#The attribute that the user wants to search on.
		#Only one can be selected
		self.searchAttribute = OptionMenu(self.root, self.sc_variable, *SEARCH_CATEGORIES)
		self.searchAttribute.grid(row = 8, column = 1, sticky = W)

		Label(self.root, text = "Select an attribute to search on: ").grid(row = 8, columnspan = 2, sticky = W)
		
		self.button = Button(self.root, text = "OK", command = self.onButtonDown)
		self.button.grid(row = 10, column = 0, columnspan = 3, pady = 20 )
		
		
		#output text
		self.text = Text(self.root)
		self.text.grid(row = 11, column = 0, columnspan = 3)
		self.text.insert(INSERT, "Output goes here")

		self.runAgainButton = Button(self.root, text = "Select another category", command = self.runAgain)
		self.returnButton = Button(self.root, text = "Return to main menu", command = self.returnToMenu)

		self.runAgainButton.grid(row = 12, column = 0)
		self.runAgainButton.grid_remove()
		
		self.returnButton.grid(row = 12, column = 1)
		self.returnButton.grid_remove()
		
		
		                           
		#-----------------------------------------------------------------------
		#end of gui formation
		
		
		
		
		
	
		
		
		
		#a dictionary that we will use to match the search criteria with
		#its attribute in the database
		self.stringDictionary = {}
		
		self.inputValidated = False
		self.MODE = 1;
		
		self.root.mainloop()
		

	
		
	def onButtonDown(self):

		print self.MODE
		
		#in the first mode the user is inputting data into the search fields
		if self.MODE == 1:
			#ensure that at least one search criteria has been entered
			self._validateInput()
		
			if self.inputValidated:
				#create a dictionary that will contain structured queries
				self._createStringDictionary()

				if self.filter_type_variable.get() == "Highest":
					orderVariable = " Desc "
				else:
					orderVariable = " Asc "

				#!usrInput = str(self.resultsVariable.get())
				usrInput = str(self.results.get())

				print "user input: ", usrInput
				print "TYPE: ", type(usrInput)
				
				print "SC_variable", self.sc_variable
				print "type of sc_variable", type(self.sc_variable)

				formattedSearchAttribute = self._formatText(str(self.sc_variable.get()))

				#depending on whether the user inputs any search
				#criteria we may not need a where clause
				optionalWhere = " WHERE "
				if (self.bt_variable.get()\
				    ==self.pricing_cat_variable.get()\
				    ==self.make_variable.get()\
				    =="Leave blank"):
					#if all search criteria are empty exclude the where
					optionalWhere = ""
				
				#self.curs.execute("Select base_model, extended_model_name, " + \
				                  #formattedSearchAttribute +\
				                  #" FROM car WHERE " + \
				                  #self.stringDictionary[1] + \
				                  #self.stringDictionary[2] + \
				                  #self.stringDictionary[3] + \
				                  #" ORDER BY " +\
				                  #formattedSearchAttribute +\
				                  #orderVariable + \
				                  #"FETCH FIRST " +\
				                  #usrInput + \
				                  #" ROWS ONLY;"  )
				
				#the main query
				self.curs.execute("SELECT base_model, extended_model_name, " + \
				                  formattedSearchAttribute +\
				                  " FROM car " + optionalWhere +\
				                  self.stringDictionary[1] + \
				                  self.stringDictionary[2] + \
				                  self.stringDictionary[3] + \
				                  " ORDER BY " +\
				                  formattedSearchAttribute +\
				                  orderVariable + ";")

				#" FROM car WHERE " + \

				#print "THE QUERY: ", self.curs.fetchall()
				
				self._processResults(self.curs.fetchall())
				

		#elif self.MODE == 2:

	
	
	#We will have to process the results based on whether 			
	def _processResults(self, carList):

		self.returnButton.grid()
		self.runAgainButton.grid()
		
		
		#printC = 0
		
		#while printC < 20:
			#print str(printC + 1), carList[printC]
			#printC += 1
		
		
		self.text.delete(1.0, END)
		
		#option 1: the search did not yield any results
		if len(carList) == 0:
			self.text.insert(INSERT, "Sorry, your search did not yield any results\n")
			self.text.insert(INSERT, "Please choose an option below\n")
			self.MODE += 1
			return
		
		#!elif len (carList) < int(self.resultsVariable.get()):
		elif len (carList) < int(self.results.get()):
			self.text.insert(INSERT, "There were not enough entries in the database to\
			meet your request. The available entries will be displayed\n")
			
			
			self.text.insert(INSERT, "There was an exact match for your query\n")
			#self.text.insert(INSERT, "Original request: " + self.resultsVariable.get() + '\n')
			self.text.insert(INSERT, "Original request: " + self.results.get() + '\n')

			counter = 1
			for line in carList:
				self.text.insert(INSERT, "(" + str(counter) + ")" + line[0] + " " \
				                 + line[1] + " " + str(line[2]) + '\n')
				counter += 1
			
			self.text.insert(INSERT, ('-' * 90) +'\n' )
			self.text.insert(INSERT, "Please choose an option below\n")
			#advance the mode
			self.MODE += 1
			return
			
		#from here there are two options. The user requested n tuples.
		#If the relevant attribute for the n + 1 tuple matches the nth 
		#tuple(e.g. the horsepower for the 30th and the 31st result is 
		#300 in both cases) the decision of which to exclude from the 
		#display would be arbitrary. In this case we want to let the 
		#user know that there are extra tuples that match on the last 
		#entry and include these in the output. If the result is such 
		#that there are no matches after the last tuple(i.e. the nth 
		#and n + 1 tuples have different values for the relevant attribute)
		#let the user know and include the exact amount that was requested

		#lastEntry = int(self.resultsVariable.get()) - 1 
		
		lastEntry = int(self.results.get()) - 1 #the actual index
		indexChanged = False
		
		
		#in each tuple in our list, index 2 is the attribute that the user
		#searched on
		while ( carList[lastEntry][2] == carList[(lastEntry + 1)][2] ):
			indexChanged = True
			lastEntry += 1 #change the last entry to include the next tuple
		 
		
		#print "!!!!!!", carList[10][2] == carList[11][2]
		print lastEntry

		if indexChanged:
			self.text.insert(INSERT, "There were additional matches for your query.\
			Extra results will be displayed:\n")
			#self.text.insert(INSERT, "Original request: " + self.resultsVariable.get() + '\n')
			self.text.insert(INSERT, "Original request: " + self.results.get() + '\n')
			self.text.insert(INSERT, "The amount displayed: " + str(lastEntry + 1) + '\n')
		else:
			self.text.insert(INSERT, "There was an exact match for your query." + '\n')
			#self.text.insert(INSERT, "Original request: " + self.resultsVariable.get() + '\n')
			self.text.insert(INSERT, "Original request: " + self.results.get() + '\n')
			
		counter = 1
		for line in carList:
			
			
			self.text.insert(INSERT, "(" + str(counter) + ")" + line[0] + " " \
			                 + line[1] + " " + str(line[2]) + '\n')
			

			
			
			if counter == lastEntry + 1:
				break
			
			counter += 1
		
		self.text.insert(INSERT, ('-' * 75) +'\n' )
		self.text.insert(INSERT, "Please choose an option below\n")
		self.MODE += 1
		return
		
	def runAgain(self):

		print "in run again"
		
		self.text.delete(1.0, END)
		self.results.delete(0, END)
		
		self._updateMode()
		self.inputValidated = False
		
		
		self.runAgainButton.grid_remove()
		self.returnButton.grid_remove()
		
		
		
		
		
		
	def returnToMenu(self):
		print "in return to menu"	
		self.mainMenu.rad1.config(state=ACTIVE)
		self.mainMenu.rad2.config(state=ACTIVE)
		self.mainMenu.rad3.config(state=ACTIVE)
		self.mainMenu.select.config(state=ACTIVE)
		self.root.destroy()
	
	#a helper method that specifically formats strings by modifying the more
	#aesthetically pleasing text as seen in the gui into a format that matches
	#the attribute name in the database
	def _formatText(self, string):
		string = string.lower()
		
		stringList = string.split(" ") #split on whitespace
		formattedString = '_'.join(stringList) #create a '_' between each word as in database
		
		return formattedString
				
	#ensures that at least one search criteria has been entered
	def _validateInput(self):
		
		

		try:

			#isNotaChar = int(self.resultsVariable.get())
			
			#if ( int(self.resultsVariable.get()) != float(self.resultsVariable.get()) ):
				#raise ValueError
			#print "!!!!!!!!!!!!!INPUT VALID"
			#self.inputValidated = True
			
			isNotaChar = int(self.results.get())
			
			if ( int(self.results.get()) != float(self.results.get()) ):
				raise ValueError
			print "!!!!!!!!!!!!!INPUT VALID"
			self.inputValidated = True
		
		except ValueError:
			#print "the entry:", self.resultsVariable.get()
			print "the entry:", self.results.get()
			tkMessageBox.showwarning("Invalid input", "You must input an integer")
			print 'You must input an integer'
			             
		
		
		#if (self.bt_variable.get()\
		    #==self.pricing_cat_variable.get()\
		    #==self.make_variable.get()\
		    #=="Leave blank"):
			#tkMessageBox.showwarning("Incorrect input", "You have to select at least one search criteria")
			
		#else:
			#print "INPUT VALID"
			#self.inputValidated = True

	
	#method creates a dictionary of strings in which the values are queries
	#based that are created based upon user input for the search criteria
	#at least one of these is already guaranteed to be nonempty in the verification step
	def _createStringDictionary(self):
		
		
		#self.bt_variable

		#self.pricing_cat_variable 
		
		#self.make_variable 
		
		
		
		
		if ( self.bt_variable.get() != "Leave blank" ):
			body_type = self.bt_variable.get()

			if ( self.pricing_cat_variable.get() != "Leave blank" or\
			    self.make_variable.get() != "Leave blank" ):
				
				#if either one of the two variables to follow
				#is not to be left empty, we must append an 'and'
				#onto this first query
				self.stringDictionary[1] = "body_type = '" + body_type + "' and " 
			    
			else:
				#otherwise this is our only query
				self.stringDictionary[1] = "body_type = '" + body_type + "'"
				
				
		else:
			#if this field is empty simply make this entry in the 
			#dictionary an empty string
			self.stringDictionary[1] = ""
			
			
		if ( self.pricing_cat_variable.get() != "Leave blank" ):
			price_category = self.pricing_cat_variable.get()
			
			
			if (self.make_variable.get() != "Leave blank"):
				
				#Now we must only check to see if the make field
				#is not empty in which case we must add an "and"
				#after this query
				self.stringDictionary[2] = "price_category = '" + price_category + "' and "
				
			else:
				#exclude the "and"
			
				self.stringDictionary[2] = "price_category = '" + price_category + "'"
				
		else:
			self.stringDictionary[2] = ""
			
			
		if (self.make_variable.get() != "Leave blank"):
			make = self.make_variable.get()
			
			#in this case there will never be an "and" after this query
			self.stringDictionary[3] = "make = '" + make + "'"
			
		else:
			self.stringDictionary[3] = ""
	
			
			
	def _updateMode(self):
		self.MODE = ( self.MODE % 2 ) + 1


		
##uncomment here to run
#driver = carCategorize()


