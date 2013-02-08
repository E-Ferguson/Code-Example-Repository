#!/usr/bin/python

from Tkinter import *# Tk, Frame, BOTH

from carMatch import *
from carLookup import *
from carCategorize import *


class MainMenu(Frame):
  
    def __init__(self, parent):
        Frame.__init__(self, parent)
        self.parent = parent
        self.initWelcomeUI()
    
    def initWelcomeUI(self):
        self.parent.title("Welcome!")
        self.grid()
        
        self.info = Label(self,text="Welcome to the car selection app. This application allows you to search to search for 2012 cars. This application will provide you with the address of any dealership that sells the car you have selected, so long as it's close to Williamsburg Virginia. It will even give you the numbers of the sales associates who work there. How handy is that?", wraplength=400,justify="left")#,width=50)
        self.info.grid(column=0,row=0,padx=5,pady=5)
        
        self.toSelect = Button(self,text="continue",command=self.onPressToSelect)
        self.toSelect.grid(column=0,row=1,pady=5)
    
    def initSelectUI(self):
        self.parent.title("Main Menu")
        
        self.option = IntVar()
        self.rad1 = Radiobutton(self,text="stage 1",variable=self.option,value=0,command=self.onStageSelected)
        self.rad2 = Radiobutton(self,text="stage 2",variable=self.option,value=1,command=self.onStageSelected)
        self.rad3 = Radiobutton(self,text="stage 3",variable=self.option,value=2,command=self.onStageSelected)
        self.rad1.grid(column=0,row=1)
        self.rad2.grid(column=0,row=2)
        self.rad3.grid(column=0,row=3)
        self.onStageSelected() # force the listener to run so appropriate message is displayed
        
        self.select = Button(self,text="select",command=self.onPressSelect)
        self.select.grid(column=0,row=4)
        
        
    def onPressToSelect(self):
        self.toSelect.grid_forget()
        self.initSelectUI()
        
    def onPressSelect(self):
        #print self.option.get()
        self.rad1.config(state=DISABLED)
        self.rad2.config(state=DISABLED)
        self.rad3.config(state=DISABLED)
        self.select.config(state=DISABLED)
        
        subRoot = Tk()
        #currentApp = Stage(subRoot, self)

        if self.option.get()==0:
            currentApp = carLookup(subRoot, self)
        elif self.option.get() == 1:
            currentApp = carMatch(subRoot, self)
        elif self.option.get() == 2:
            self.currentApp = carCategorize(subRoot, self)
        subRoot.mainloop()
    
    def onStageSelected(self):
        if self.option.get() == 0:
            self.info.config(text="stage 1 is selected")
        elif self.option.get() == 1:
            self.info.config(text="stage 2 is selected")
        elif self.option.get() == 2:
            self.info.config(text="stage 3 is selected")
        else:
            self.info.config(text="select a stage:")
            
class Stage(Frame):
    def __init__(self,parent,mainMenu):
        Frame.__init__(self, parent)
        self.parent = parent
        self.mainMenu = mainMenu
        self.initialize()
        
    def initialize(self):
        self.parent.title("test")
        self.grid();
        self.button = Button(self,text="back to main menu",command=self.returnToMainMenu)
        self.button.grid(column=0,row=0)
        
    def returnToMainMenu(self):
        self.mainMenu.rad1.config(state=ACTIVE)
        self.mainMenu.rad2.config(state=ACTIVE)
        self.mainMenu.rad3.config(state=ACTIVE)
        self.mainMenu.select.config(state=ACTIVE)
        self.parent.destroy()
        
        
        

def main():
  
    root = Tk()
    #root.geometry("350x175+300+300")
    app = MainMenu(root)
    root.mainloop()  


if __name__ == '__main__':
    main()  
