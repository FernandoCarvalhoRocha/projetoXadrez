package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca {

	private Cores cor;
	private int contadorDeMovimentos;

	public PecaDeXadrez(Tabuleiro tabuleiro, Cores cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cores getCor() {
		return cor;
	}
	public int getcontadorDeMovimentos() {
		return contadorDeMovimentos;
	}
	
	public void aumentaOContadorDeMovimentos() {
		contadorDeMovimentos++;
	}
	public void diminuiOContadorDeMovimentos() {
		contadorDeMovimentos--;
	}

	public PosicaoXadrez getPosicaoXadrez() {
		return  PosicaoXadrez.fromPosicao(posicao);	
	}

	protected boolean existePecaAdiversaria(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez) getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
