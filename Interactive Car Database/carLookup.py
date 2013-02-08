#!/usr/bin/python


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




class carLookup(Frame):
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
	
		#x = curs.execute("SELECT * FROM company")
		

		#self.companies = ['Acura', 'Buick', 'BMW']
		

		#self.root = Tk()

		#hookup to main menu#
		Frame.__init__(self, parent)
		
		self.root = parent
		self.mainMenu = mainMenu
		
		self.root.title ( "Lookup cars" )
		self.grid();
		#-------------------#
		

		
		
		Label(self.root, text = ("="*34) + "CAR LOOKUP" + ("="*34) ).grid(row = 0, column = 1, columnspan = 3, sticky = W)
		
		self.dataEntry = Entry(self.root, state = DISABLED)
		self.dataEntry.grid(row = 1, column = 1, columnspan = 3 )
	
		self.text = Text(self.root)
		self.text.grid(row = 2, column = 1, columnspan = 3)
	
		self.button = Button(self.root, text = "OK", command = self._onButtonDown)
		self.button.grid(row = 3, column = 1, columnspan = 3)
	
		self.frame = Frame(self.root, width = 100, height = 100)
		self.frame.bind_all("<Key>", self.callback)
		
		#self.frame.grid()

		#mode 0 is purely a startup mode and will won't be entered
		#into again while this instance exists
		self.MODE = 1
		
		#two general purpose containers that will be used throughout 
		#program execution. Their values will change during the course
		#of execution
		self.generalList = []
		self.generalDict = {}

		
		self.companyLookupButton = Button(self.root, text = "Lookup Company", command = self._goToCompany)
		self.companyLookupButton.grid(row = 2, column = 1, columnspan = 3)
		self.companyLookupButton.grid_remove()

		self.dealershipLookupButton = Button (self.root, text = "Lookup Dealership", command = self._goToDealership)
		self.companyLookupButton.grid(row = 3, column = 1, columnspan = 3)
		self.companyLookupButton.grid_remove()
				
		self.runAgainButton = Button(self.root, text = "Start again", command = self._runAgain, state = DISABLED)
		self.runAgainButton.grid(row = 6, column = 1, columnspan = 3, padx = 3)
		#self.runAgainButton.grid_remove()
		
		self.menuButton = Button(self.root, text = "Main menu", command = self._returnToMenu, state = DISABLED)
		self.menuButton.grid(row = 7, column = 1, columnspan = 3)
		#self.menuButton.grid_remove()

		self.onStartup = True
		
		#self._startup()
		self.main()

		
		
		self.root.mainloop()
		
	def test(self):
		print "button pressed"
		
	#where the mode is updated on each button press after verification
	#that the selection is valid		
	def _onButtonDown(self):

		#if self.MODE == 0:
			#self.text.delete(1.0, END)
			#self.updateMode()
			#self.main()
		
		if self.MODE == 1:
		

			if not(self.onStartup):
				
				
			
				currentString = self.text.get(1.0, END)
				currentStringList = currentString.split('\n')
		    
				while '' in currentStringList:
					currentStringList.remove('')
		    
				currentEntryLength = len(currentStringList)
		    
				if currentEntryLength != 1:
				
					tkMessageBox.showwarning("Incorrect attempt", "You cannot proceed until only " +\
						                 "one comapny is selected")
			    
				elif currentEntryLength == 1:
				
					#print "the current stringList: ", currentStringList
			    
					#print "The count:", len(currentStringList)
		
					#x = curs.execute("SELECT DISTINCT base_model FROM car where make = '" + currentStringList[0] + "';")
				
					#modelList = []  
				
					#for line in curs.fetchall():
						#for row in line:
							#modelList.append(row)
				
					#text.delete(1.0, END)
					#text.insert( INSERT, '\n'.join(modelList) )
	
					
					#updating the mode
	
					print "Entry valid proceeding to mode 2"
					
					self.updateMode()
					self.main()
					
				
			else:
				self.text.delete(1.0, END)
				self.onStartup = False
				self.dataEntry.config(state = NORMAL)
				self.runAgainButton.config(state = NORMAL)
				self.menuButton.config(state = NORMAL)
				self.main()
				
				
				
				
				
				
		elif self.MODE == 2:

			currentString = self.text.get(1.0, END)
			currentStringList = currentString.split('\n')
	    
			while '' in currentStringList:
				currentStringList.remove('')
	    
	
			currentEntryLength = len(currentStringList)
	    
			if currentEntryLength != 1:
			
				tkMessageBox.showwarning("Incorrect attempt", "You cannot proceed until only " + \
				"one car model is selected")
				
			elif currentEntryLength == 1:
				
				print "Entry valid, proceeding to mode 3"
				
				self.updateMode()
				print self.MODE
				self.main()
		
		elif self.MODE == 3:
			
			try:
				inputNumber = int(self.dataEntry.get())
				print inputNumber
				
				#the generalDict with keys corresponding the numbers on the screen
				#and values that are the car ids of the car associated with each number should be made.
				#This is a simple test that the value is in our dictionary
				keyTester = self.generalDict[inputNumber]
				
				print "Entry valid, proceeding to mode 4"
				self.updateMode()
				self.main()
				
				
				
			
			except ValueError:
				self.text.insert(INSERT, "Sorry you must input a number\n")
				self.dataEntry.delete(0, END)
			
			except KeyError:
				self.text.insert(INSERT, "Sorry, your number is not in the correct range\n")
				self.dataEntry.delete(0, END)
		
		
		#in mode 4 the user is looking at formatted output. There
		#is no input to verify
		elif self.MODE == 4:
			self.updateMode()
			self.main()
			

		elif self.MODE == 5:

			try:
				selectedOption = int(self.dataEntry.get())

				validList = [1,2,3,4]

				
				
				if selectedOption not in validList:
					raise IndexError
				
				
				#self.text.grid_remove()
				#self.button.grid_remove()
				
				
				#self.companyLookupButton.grid()
				#self.dealershipLookupButton.grid()
				#self.self.runAgainButton.grid()
				#self.menuButton.grid()

				
				
				
				
				#print "Proceeding to mode 6"
				#self.updateMode()
				#self.main()
				
				                     
			except ValueError:
				tkMessageBox.showwarning("Incorrect input", "Your selection must be a valid integer in the correct range")
			except IndexError:
				tkMessageBox.showwarning("Incorrect input", "Your selection must be a valid integer in the correct range")
			
			
			
	def callback(self, event):

		
		#it is during mode 3 that the user will input a number. we do 
		#not want to use strings and our general list during this step
		if self.MODE < 3:
			self.text.delete(1.0, END)
		    
		
			currentString = self.dataEntry.get()
		
			print "the current string:", currentString
		    
			stringToDisplay = self.makeString(self.generalList, currentString)
		    
			self.text.insert(INSERT, stringToDisplay)
			#text.pack()
			#print "The pressed button", event.char
			
		

	#where most of the queries occur based whatever 'mode' we are in.
	#the idea of a mode is centered around the data the the user is trying 
	#to access so that in mode 1 the user is accessing a company, in mode 2
	#a certain base model, and so forth.
	def main(self):


		#if self.MODE == 0:
			
			#self.text.insert(INSERT, ("=" * 34) + "CAR LOOKUP" + ("=" * 36) )
			#self.text.insert(INSERT, (" " * 579) +"Welcome to the car lookup user interface\n")
			#self.text.insert(INSERT, "\n")
			#self.text.insert(6.5, (" " * 9) + "Use the entry field above to narrow your search " +\
			#"and press OK when\n") 
			#self.text.insert(INSERT, (" " * 23) + " you've made a single selection\n")
			
			#self.text.insert(INSERT, ("\n" * 2))
			#self.text.insert(INSERT, (" " * 28) + "Press OK to continue")
		
		#in mode 1 the user is selecting a certain car company
		if self.MODE == 1:

			if not(self.onStartup):
				print "BACK IN MODE 1"
				#in this mode we want to 
				self.curs.execute("select name from company;")
		
				for line in self.curs.fetchall():
					for row in line:
						self.generalList.append(row)
						
				#the initial posting of the cars		
				self.text.insert(INSERT, '\n'.join(self.generalList))
			else:
				

				#self.text.insert(INSERT, ("=" * 34) + "CAR LOOKUP" + ("=" * 36) )
				self.text.insert(INSERT, (" " * 579) +"Welcome to the car lookup user interface\n")
				self.text.insert(INSERT, "\n")
				self.text.insert(6.5, (" " * 9) + "Use the entry field above to narrow your search " +\
			        "and press OK when\n") 
				self.text.insert(INSERT, (" " * 23) + " you've made a single selection\n")
				
				self.text.insert(INSERT, ("\n" * 2))
				self.text.insert(INSERT, (" " * 28) + "Press OK to continue")

				
				
				
		
		#in mode 2 the user is selecting a certain model that is offered
		#by a company
		elif self.MODE == 2:
			
			#clear the data entry field
			self.dataEntry.delete(0, END)
			
			currentString = self.text.get(1.0, END)
			currentStringList = currentString.split('\n')
			
			self.generalList = []
			self.curs.execute("SELECT DISTINCT base_model FROM car where make = '" + currentStringList[0] + "';")
		
		 
		
			for line in self.curs.fetchall():
				for row in line:
					self.generalList.append(row)
			
			self.text.delete(1.0, END)
			self.text.insert( INSERT, '\n'.join(self.generalList) )
		
		
		#in mode 3 we will be displaying the model variations (such as 
		#automatic or convertible)
		elif self.MODE == 3:
			
			self.dataEntry.delete(0, END)

			baseCarModel = str(self.text.get(1.0, END))
			baseCarModel = baseCarModel.strip('\n')
			
			print type(baseCarModel)

			print "BASE CAR MODEL IN MODE 3: ", baseCarModel 
			
			self.generalList = []
			self.curs.execute("SELECT car_id, base_model, extended_model_name FROM car where base_model = '" + baseCarModel + "';")
			#self.curs.execute("SELECT base_model, extended_model_name FROM car WHERE base_model = '" + baseCarModel + "';")


			#print "THE CURSOR:", self.curs.fetchall()

			
			#What we want to do here is create a dictionary in which the key 
			#is the number beside the output for each entry and the value is the car_id.
			#this will allow for easy lookup of the relevant car attributes in the next step.
			#at the same time we are also extracting the extended_model_name and matching it
			#with the base model
			counterStartingPoint = 1
			counter = 1
			for line in self.curs.fetchall():
				self.generalDict[counter] = line[0] #create a dictionary with: key = counter; value = car_id (line[1])
				self.generalList.append( "(" + str(counter) + ") " + ' '.join(list(line[1:]))) #disregarding the car_id field--use list(line) to debug
				counter += 1
				

			
			

			
			
			print "The generalList in MODE 3:", self.generalList
			print "The generalDict in MODE 3:", self.generalDict
			
			self.text.delete(1.0, END)
			
			self.text.insert(INSERT, "The specific model variations for the base model that you have selected are:")
			self.text.insert(INSERT, "\n")
			
			self.text.insert(INSERT, '\n'.join(self.generalList) )
			
			
			self.text.insert(INSERT, "\nPlease select the number corresponding to the model you are interested in\n")
			
			
			
		#In mode 4 we will be displaying formatted output corresponding to
		#the relevant attributes in the car table that correspond to the
		#car selection that the user has made
		elif self.MODE == 4:
			self.text.delete(1.0, END)

			
			
			#should be verified as an integer when the button 
			#was pressed in onButtonDown()
			
			selectedOption = int(str(self.dataEntry.get()))

			#save the car_id of the current selection in the generalList 
			#for future access
			
			self.generalList = [self.generalDict[selectedOption]]
			
			#self.dataEntry.delete(0, END)
			
			#in the last step a dictionary was created in which the 
			#integers that corresponded to the available options for 
			#car selection were placed in a dictionary in which the 
			#value were the unique car_ids for each available car
			#we can now access that unique car id using the dictionary
			#and display all relevant attributes in the database
			
			self.curs.execute("SELECT * FROM car WHERE car_id = '" + self.generalDict[selectedOption] + "';")
			
			entry = list(self.curs.fetchall()[0])
			
			print "entry", entry
			print "LENGTH" , len(entry)
			#print "THE CURSOR IN MODE 4:", self.curs.fetchall()

			self.text.insert(INSERT, "The entry for the " + entry[6] + " ," + entry[1] +" "+ entry[11] + "is as follows:")
			self.text.insert(INSERT, "\n")
			
			self.text.insert(INSERT, "Cargo room: " + str(entry[2]) + '\n')
			self.text.insert(INSERT, "Horsepower: " + str(entry[3]) + '\n')
			self.text.insert(INSERT, "Service Cost: " + str(entry[4]) + '\n')
			self.text.insert(INSERT, "Insurance Cost: " + str(entry[5]) + '\n')
			self.text.insert(INSERT, "Body-type: " + entry[7] + '\n')
			self.text.insert(INSERT, "City MPG: "  + str(entry[8]) + '\n')                
			self.text.insert(INSERT, "Highway MPG: " + str(entry[9]) + '\n')
			self.text.insert(INSERT, "Fuel type: " + entry[10] + '\n')                
			self.text.insert(INSERT, "MSRP: "  + str(entry[12]) + '\n')       
			self.text.insert(INSERT, "Price cateogry: " + entry[13] +'\n')
			

			
			self.text.insert(INSERT,"-----------------------------\n")
			self.text.insert(INSERT, "Press OK to continue\n")
			
		#in mode 5 the user has already selected a car and seen its relevant
		#attributes
		elif self.MODE == 5:

			
			
			
			
			self.text.delete(1.0, END)
			self.dataEntry.delete(0, END)

			self.button.grid_remove()
			
			self.dataEntry.delete(0, END)
			#no need for input for the next several steps
			self.dataEntry.config(state = DISABLED)
			
			self.text.insert(INSERT, "From here you have one of four options: \n")
			self.text.insert(INSERT, "(1) Lookup the company information for the car you have selected\n")
			self.text.insert(INSERT, "(2) Lookup local dealerships that sell the car you have selected\n")
			self.text.insert(INSERT, "(3) Lookup another car\n")
			self.text.insert(INSERT, "(4) Return to main menu\n")
			
				
				
			self.companyLookupButton.grid()
			self.dealershipLookupButton.grid()
			self.runAgainButton.grid()
			self.menuButton.grid()

			
			
			
			#self.text.insert(INSERT, "From here you have one of four options: \n")
			#self.text.insert(INSERT, "(1) Lookup the company information for the car you have selected\n")
			#self.text.insert(INSERT, "(2) Lookup local dealerships that sell the car you have selected\n")
			#self.text.insert(INSERT, "(3) Lookup another car\n")
			#self.text.insert(INSERT, "(4) Return to main menu\n")
		
			
		##in mode 6 the user can continue accessing information pertaining
		##to the selection made in mode 4. The options are also available 
		##to select another car or return to the main menu. 
		#elif self.MODE == 6:
			
			#usrInput = int(self.dataEntry.get())

			#self.text.delete(1.0, END)
			
			##The first two options involve making further queries. 
			##In order to accomplish this, the car_id of the selected
			##vehicle was saved in the generalList for access in mode
			##4

			#self.text.grid_remove()
			
			#if usrInput == 1:

				#print "The general list in mode 6: ", self.generalList
				#print "The general dictionary in mode 6: ", self.generalDict

				#self.curs.execute("SELECT make FROM car WHERE car_id = " + self.generalList[0])
				#make = self.curs.fetchall()[0]
				#self.curs.execute("SELECT * FROM company WHERE make = " + make)
				
				#self.text.insert(INSERT, "You have selected to view the company information for the model you selected")

				#companyInfo = self.curs.fetchall()
				
				#self.text.insert(INSERT, "Make: " + companyInfo[0][0] + '\n')
				#self.text.insert(INSERT, "Country: " + companyInfo[0][1] + '\n')
				#self.text.insert(INSERT, "Region: " + company[0][1] + '\n')
				
				##self.curs.execute
			

			
			
	
	def updateMode(self):
		self.MODE = ( self.MODE % 5 ) + 1
		
		
	#matches some current string to strings that are stored in a list
	def makeString (self, stringList, stringToMatch):
	    
		result = ""
		stringLength = len(stringToMatch)
	
		completedList = []

		
		
		for string in stringList:
			print "String: #", string +"#"
			print "StringToMatch: #", stringToMatch + "#"
			print string.lower() in stringToMatch.lower()
			print stringToMatch.lower() in string.lower()
		
		#the length of the string that we are comparing to must be less than  
		#whatever string we are considering in the list 
			if ( len (stringToMatch)  < len (string) and string not in completedList ):
		    
		    #constructing a partial string
				partialString = string [0:stringLength]
		    
				#a case-insensitive comparison
				if partialString.lower() == stringToMatch.lower(): 
					result += string + "\n"
					completedList.append(string)
			
			#if the two are directly equal
			elif ( string.lower().strip(" ") \
			     == stringToMatch.lower().strip(" ") ):
				result += string + "\n"
		
		return result
			
	
	
	
	def _goToCompany(self):

		self.text.delete(1.0, END)
		
		print "The general list in mode 6: ", self.generalList
		print "The general dictionary in mode 6: ", self.generalDict

		self.curs.execute("SELECT make FROM car WHERE car_id = '" + self.generalList[0] + "';")
		make = self.curs.fetchall()[0]
		self.curs.execute("SELECT * FROM company WHERE name = '" + make[0] + "';")
		
		self.text.insert(INSERT, "You have selected to view the company information for the model you selected\n")

		companyInfo = self.curs.fetchall()

		#print "The company info: ", companyInfo
		
		self.text.insert(INSERT, "Make: " + companyInfo[0][0] + '\n')
		self.text.insert(INSERT, "Country: " + companyInfo[0][1] + '\n')
		self.text.insert(INSERT, "Region: " + companyInfo[0][2] + '\n')
		
		#self.curs.execute
		
	def _goToDealership(self):
		x= 3
		
		
	def _runAgain(self):
		#self.updateMode
		print "MODE BEFORE: ", self.MODE
		self._resetMode()
		print "MODE AFTER: ", self.MODE
		
		self.text.delete(1.0, END)
		self.dataEntry.config(state = NORMAL)
		self.dataEntry.delete(0, END)
		self.generalList = [] #clear the generalList
		
		
		self.companyLookupButton.grid_remove()
		self.dealershipLookupButton.grid_remove()
		
		#self.runAgainButton.grid_remove()
		#self.menuButton.grid_remove()
		
		self.button.grid()
		
		self.main()
		
	def _returnToMenu(self):
		self.mainMenu.rad1.config(state=ACTIVE)
		self.mainMenu.rad2.config(state=ACTIVE)
		self.mainMenu.rad3.config(state=ACTIVE)
		self.mainMenu.select.config(state=ACTIVE)
		self.root.destroy()
	
	
	def _resetMode(self):
		self.MODE = 1
	
	    
			
	


##uncomment here to run	
#driver = carLookup()

	

