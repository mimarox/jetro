package net.sf.jetro.visitor.pathaware;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

public class DummyVisitingReader implements VisitingReader {

	@Override
	public void accept(JsonVisitor<?> visitor) {
		// {
		JsonObjectVisitor<?> rootVisitor = visitor.visitObject();

		// {"key":
		rootVisitor.visitProperty("key");

		// {"key":[
		JsonArrayVisitor<?> keyVisitor = rootVisitor.visitArray();

		// {"key":[null
		keyVisitor.visitNullValue();

		// {"key":[null,2.0
		keyVisitor.visitValue(2.0);

		// {"key":[null,2.0,"<&>"
		keyVisitor.visitValue("<&>");

		// {"key":[null,2.0,"<&>",true
		keyVisitor.visitValue(true);

		// {"key":[null,2.0,"<&>",true,[
		JsonArrayVisitor<?> key_4_Visitor = keyVisitor.visitArray();

		// {"key":[null,2.0,"<&>",true,[">&<"
		key_4_Visitor.visitValue(">&<");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"
		key_4_Visitor.visitValue("bar");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"]
		key_4_Visitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{
		JsonObjectVisitor<?> key_5_Visitor = keyVisitor.visitObject();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":
		key_5_Visitor.visitProperty("key");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"
		key_5_Visitor.visitValue("äöü");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}
		key_5_Visitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}]
		keyVisitor.visitEnd();

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo"
		rootVisitor.visitProperty("foo");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo":"bar"
		rootVisitor.visitValue("bar");

		// {"key":[null,2.0,"<&>",true,[">&<","bar"],{"key":"äöü"}],"foo":"bar"}
		rootVisitor.visitEnd();

		// finalize visiting
		visitor.visitEnd();
	}
}