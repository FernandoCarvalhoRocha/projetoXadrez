package xadrez;

import tabuleiro.Posicao;

public class PosicaoXadrez {
	
	private Character coluna;
	private int linha;
	public PosicaoXadrez(char coluna, int linha) {
		if (coluna < 'A' || coluna > 'H' || linha < 1 || linha > 8) {
			throw new XadrezException("Erro ao iniciar as posi��es. Os valores Validos V�o de A1 � H8");
		}
		this.coluna = coluna;
		this.linha = linha;
	}
	public char getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}

	protected Posicao toPosicao() {
		return new Posicao(8 - linha, coluna - 'A');
		
	}
	protected static PosicaoXadrez fromPosicao(Posicao posicao) {
		return new PosicaoXadrez((char) ('A' + posicao.getColuna()),8 - posicao.getLinha());
	}
	
	@Override
	public String toString() {
		return "" + coluna + linha;
	}
	
}
