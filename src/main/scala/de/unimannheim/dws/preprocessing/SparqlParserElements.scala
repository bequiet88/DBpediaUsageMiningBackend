package de.unimannheim.dws.preprocessing

class SparqlParserElement(val token:String) {
 def getValue = token  
}

class TriplePatternElement(override val token:String) extends SparqlParserElement(token) {}

class Variable(override val token:String) extends TriplePatternElement(token) {}

class Literal(override val token:String) extends TriplePatternElement(token) {}

class Uri(override val token:String) extends TriplePatternElement(token) {}

class EndOfTriple(override val token:String) extends SparqlParserElement(token) {}

class Semicolon(override val token:String) extends SparqlParserElement(token) {}

class Noise(override val token:String) extends SparqlParserElement(token) {}
