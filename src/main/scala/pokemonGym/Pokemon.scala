package pokemonGym

/**
 * Created by leandromoras on 6/8/15.
 */
case class Pokemon(
		energia: Int,
		energiaMaxima: Int,
		peso: Int,
		fuerza: Int,
		velocidad: Int,
    genero: Char,
    especie: Especie,
    // TODO es más facil usar un mapa (que de hecho también es una función "key => valor") 
    ataques: List[(Ataque, Int)],
    // TODO el nivel está en función de la experiencia y la especie (debería ser calculado)
    nivel: Int = 1,
    experiencia: Int = 0) {
  
  require(validarAtaques(), "Los ataques no son ni del tipo principal $especie.tipoPrincipal" +
                            " ni del tipo secundario $especie.tipoSecundario" )

  val MAXIMA_ENERGIA_RECOBRADA = 50

  def ganarExperiencia(exp: Int) : Pokemon = {
    val nivelAnterior = getNivel(experiencia, especie.resistenciaEvolutiva ) - 1
    val expParaSubirNivel = 2 * nivelAnterior * especie.resistenciaEvolutiva + especie.resistenciaEvolutiva
    val expRestante = expParaSubirNivel - experiencia
    if(exp >= expRestante){
      copy(experiencia = expParaSubirNivel).subirDeNivel().ganarExperiencia(exp - expRestante)
    } else {
      copy(experiencia = experiencia + exp)
    }
  }

  def getNivel(experiencia:Int, resistenciaEvolutiva:Int) : Int = {
    if (experiencia > resistenciaEvolutiva) {
      val experienciaSubirEnNivelInferior = (experiencia - resistenciaEvolutiva) / 2
      1 + getNivel(experienciaSubirEnNivelInferior, resistenciaEvolutiva)
    } else {
      1
    }
  }

  def conoceAtaque(ataque: Ataque): Boolean = {
    !(ataques.find { case (atq, _) => atq == ataque }).isEmpty
  }
  
  def puntosDeAtaque(ataque: Ataque): Int = {
    ataque match {
      case atq if conoceAtaque(atq) => (ataques.find { case (atq, _) => atq == ataque }
                                               .map { case (_, puntos) => puntos}).get
      case _ => 0
    }
  }
  
  def usarAtaque(ataque: Ataque): Pokemon = {
    if (conoceAtaque(ataque)) {
      copy(ataques = (ataque, puntosDeAtaque(ataque) - 1) :: ataques.filterNot((tupl) => tupl._1 == ataque))
    } else {
      throw new IllegalArgumentException
    }
  }
  
  def valido(): Boolean = {
    var valido : Boolean = true
    valido &= fuerza > 0
    valido &= energia >= 0
    valido &= velocidad > 0
    valido &= nivel > 0
    valido &= experiencia >= 0
    valido &= peso > 0
    valido
  }
  
  def subirDeNivel() = {
      val nuevoPoke = copy(nivel = nivel + 1,
      fuerza = fuerza + especie.aumentoFuerza,
      velocidad = velocidad + especie.aumentoVelocidad,
      peso = peso + especie.aumentoPeso,
      energiaMaxima = energiaMaxima + especie.aumentoEnergiaMaxima).aumentarEnergiaAlMaximo()
      if (nuevoPoke.valido()) nuevoPoke else this
  }

  def aumentarVelocidad(aumento: Int) = copy(velocidad = velocidad + aumento)
  
  def validarAtaques(): Boolean = {
    ataques.forall((ataque) => {
      ataque._1.tipo match {
            case Normal => true
            case tipo if esDelTipo(tipo) => true
            case _ => false
          }
    })
  }
  
  def aumentarEnergiaAlMaximo() : Pokemon = copy(energia = energiaMaxima)

  def usarPocion() : Pokemon = {
    if(energiaMaxima - energia < MAXIMA_ENERGIA_RECOBRADA){
      copy(energia = energiaMaxima)
    }else{
      copy(energia = energia + MAXIMA_ENERGIA_RECOBRADA)
    }
  }

  def comerCalcio() : Pokemon = {
    copy (velocidad = velocidad + 5)
  }

  def comerZinc() : Pokemon = {
    copy( ataques = ataques.map { case (ataque: Ataque, puntos: Int) => (ataque, puntos+2)})
  }

  def recobrarPuntosAtaque() : Pokemon = {
    copy( ataques = ataques.map { case (ataque: Ataque, puntos: Int) => (ataque, ataque.maximoPuntosAtaque)})
  }

  def usarPiedra(tipoPiedra: Tipo): Pokemon ={
    val nuevaEspecie : Especie = especie.condicionEvolutiva match {
      case UsarPiedraLunar => if (tipoPiedra.equals(Lunar)) especie.evolucion.get else especie
      case UsarPiedra => if (tipoPiedra.equals(especie.tipoPrincipal)) especie.evolucion.get else especie
      case _ => especie
    }
    copy(especie = nuevaEspecie)
  }

  def evolucionar() : Pokemon = {
    copy (especie = especie.evolucion.get)
  }

  def nadar(minutos: Int) : Pokemon = {
    val experienciaGanada = minutos * 200
    val energiaPerdida = minutos
    val nuevoPoke = especie.tipoPrincipal match{
      case Agua => copy(experiencia = experiencia + experienciaGanada, energia = energia - energiaPerdida, velocidad = velocidad + 60 / minutos )
      case _ => copy(experiencia = experiencia + experienciaGanada, energia = energia - energiaPerdida)
    }
    nuevoPoke
  }
  
  def esDelTipo(tipo: Tipo) : Boolean = {
    especie match {
      case Especie(_,_,`tipo`,_,_,_,_,_,_,_) => true
      case Especie(_,_,_,Some(tipoSec),_,_,_,_,_,_) => tipoSec.equals(tipo)
      case _ => false
    }
  }
}
