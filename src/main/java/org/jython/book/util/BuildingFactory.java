/**
 * Object Factory that is used to coerce python module into a Java class
 */

package org.jython.book.util;

import org.jython.book.interfaces.BuildingType;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.Properties;

public class BuildingFactory {

    private PyObject buildingClass;

    /**
     * Create a new PythonInterpreter object, then use it to execute some python code. In this case,
     * we want to import the python module that we will coerce.
     *
     * Once the module is imported than we obtain a reference to it and assign the reference to a
     * Java variable
     */

    public BuildingFactory() {
        Properties props = new Properties();
        // set in the VM args in the Eclipse runtime configuration instead
        props.setProperty("python.path", "/Users/angelchambi/modules:scripts");
        PythonInterpreter.initialize(System.getProperties(), props,
                new String[]{""});
        PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.exec("from Building import Building");
        buildingClass = interpreter.get("Building");
    }

    /**
     * The create method is responsible for performing the actual coercion of the referenced python
     * module into Java bytecode
     */

    public BuildingType create(String name, String location, String id) {

        PyObject buildingObject = buildingClass.__call__(new PyString(name),
                new PyString(location),
                new PyString(id));
        return (BuildingType) buildingObject.__tojava__(BuildingType.class);
    }

}