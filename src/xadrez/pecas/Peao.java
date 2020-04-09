package xadrez.pecas;

import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.Cores;
import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;

public class Peao extends PecaDeXadrez {
	
	private PartidaDeXadrez partida;
	

	public Peao(Tabuleiro tabuleiro, Cores cor,PartidaDeXadrez partida) {
		super(tabuleiro, cor);
		this.partida = partida;
	}

	@Override
	public String toString() {
		return "  PEAO ";
	}
	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		Posicao p = new Posicao(0, 0);

		if (getCor() == Cores.BRANCO) {
			p.setValores(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().possuiPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() + 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) 
					&& !getTabuleiro().possuiPeca(p)
					&& getTabuleiro().posicaoExistente(p2) 
					&& !getTabuleiro().possuiPeca(p2)
					&& getcontadorDeMovimentos() == 0) {
				
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExistente(p) && existePecaAdiversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExistente(p) && existePecaAdiversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// movimento especial En Passant Branco
			if (posicao.getLinha() == 4) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExistente(esquerda)
					&& existePecaAdiversaria(esquerda)
					&& getTabuleiro().peca(esquerda) == partida.getEnPassantPossivel()){
						
						mat[esquerda.getLinha() + 1][esquerda.getColuna()] = true;
						
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExistente(direita)
					&& existePecaAdiversaria(direita)
					&& getTabuleiro().peca(direita) == partida.getEnPassantPossivel()){
						
						mat[direita.getLinha() + 1][direita.getColuna()] = true;
						
						}
			}
			
		} else {
			p.setValores(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().possuiPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() - 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) 
					&& !getTabuleiro().possuiPeca(p)
					&& getTabuleiro().posicaoExistente(p2) 
					&& !getTabuleiro().possuiPeca(p2)
					&& getcontadorDeMovimentos() == 0) {
				
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExistente(p) && existePecaAdiversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExistente(p) && existePecaAdiversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// movimento especial En Passant Preto
				if (posicao.getLinha() == 3) {
					Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() -1);
					if (getTabuleiro().posicaoExistente(esquerda)
						&& existePecaAdiversaria(esquerda)
						&& getTabuleiro().peca(esquerda) == partida.getEnPassantPossivel()){
							
							mat[esquerda.getLinha() -1][esquerda.getColuna()] = true;
							
					}
					Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() -1);
					if (getTabuleiro().posicaoExistente(direita)
						&& existePecaAdiversaria(direita)
						&& getTabuleiro().peca(direita) == partida.getEnPassantPossivel()){
							
							mat[direita.getLinha() -1][direita.getColuna()] = true;
							
							}
				}
		}
		
		return mat;
	}

}
