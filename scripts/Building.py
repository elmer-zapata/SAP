# A python module that implements a Java interface to
# create a building object
from org.jython.book.interfaces import BuildingType


class Building(BuildingType):
    def __init__(self, name, address, id):
        self.name = name
        self.address = address
        self.id = id

    def getBuildingName(self):
        return self.name

    def getBuildingAddress(self):
        return self.address

    def getBuildingId(self):
        return self.id
