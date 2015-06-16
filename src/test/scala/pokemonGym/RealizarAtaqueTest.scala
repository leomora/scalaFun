package pokemonGym

import org.scalatest._
import pokemonGym._

/**
 * @author seb
 */
class RealizarAtaqueTest extends FlatSpec with Matchers {

  "Un pokemon" should "subir exp cuando realiza ataque tipo dragon" in {
    val charizard = Pokemon(150, 150, 30, 35, 25, 'F', Especie.Charizard(), List((FuriaDragon, 4)), 55)
    val experienciaAntes = charizard.experiencia
    val charizarDespuesDeActividad = gim.ejecutar(charizard, RealizarAtaque(FuriaDragon)).pokemon
    charizarDespuesDeActividad.experiencia should be (experienciaAntes + 80)
  }
  
  "Un pokemon" should "subir exp cuando realiza ataque de su tipo principal" in {
	  val charizard = Pokemon(150, 150, 30, 35, 25, 'F', Especie.Charizard(), List((Ascuas, 4)), 55)
	  val experienciaAntes = charizard.experiencia
	  val charizarDespuesDeActividad = gim.ejecutar(charizard, RealizarAtaque(Ascuas)).pokemon
    charizarDespuesDeActividad.experiencia should be (experienciaAntes + 50)
  }
  
  "Un pokemon" should "duerme despues de Reposar" in {
	  val ratata = Pokemon(1, 1, 1, 1, 1, 'F', Especie.Ratata(), List((Reposar, 400)), 1)
	  val estadoRatataDspAct = gim.ejecutar(ratata, RealizarAtaque(Reposar))
	  estadoRatataDspAct match {
	    case Dormido(poke, contador) =>  {
        contador shouldEqual 3
      }
	  }
  }
  
  "Un pokemon" should "baja un punto de ataque despues de atacar" in {
	  val ratata = Pokemon(1, 1, 1, 1, 1, 'F', Especie.Ratata(), List((Reposar, 400)), 1)
	  val estadoRatataDspAct = gim.ejecutar(ratata, RealizarAtaque(Reposar))
	  estadoRatataDspAct match {
		  case Dormido(poke, _) =>  {
        poke match {
          case Pokemon(_, _, _, _, _, _, _, a1::cola, _, _) => a1._2 shouldEqual 399
        }
		  }
	  }
  }
  
  "Un pokemon" should "no atacar si esta dormido" in {
	  val ratata = Pokemon(1, 1, 1, 1, 1, 'F', Especie.Ratata(), List((Reposar, 400),(Mordida, 2)), 1)
	  val estadoRatataDspAct = gim.ejecutar(ratata, RealizarAtaque(Reposar), RealizarAtaque(Mordida))
	  estadoRatataDspAct match {
		  case Dormido(poke, _) =>  {
			  poke match {
			    case Pokemon(_, _, _, _, _, _, _, a1::a2::cola, _, _) => a2._2 shouldEqual 2
			  }
		  }
	  }
  }
  
  "Un pokemon" should "despertarse despues de dormir" in {
	  val ratata = Pokemon(1, 1, 1, 1, 1, 'F', Especie.Ratata(), List((Reposar, 400),(Mordida, 2)), 1)
	  val estadoRatataDspAct = gim.ejecutar(ratata, RealizarAtaque(Reposar), RealizarAtaque(Mordida), 
                                          RealizarAtaque(Mordida), RealizarAtaque(Mordida), RealizarAtaque(Mordida))
	  estadoRatataDspAct match {
  	  case OK(poke) =>  {
  		  poke match {
  		  case Pokemon(_, _, _, _, _, _, _, a1::a2::cola, _, _) => a1._2 shouldEqual 1
  		  }
  	  }
	  }
  }
}
