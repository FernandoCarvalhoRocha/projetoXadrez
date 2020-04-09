package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cores jogador;
	private Tabuleiro tabuleiro;
	private List<Peca> pecaNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapituradas = new ArrayList<>();
	private boolean xeque;
	private boolean xequeMate;
	private PecaDeXadrez enPassantPossivel;
	private PecaDeXadrez promocao;
	

	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogador = Cores.BRANCO;
		iniciarPartida();
	}

	public int getTurno() {
		return turno;
	}

	public Cores getJogador() {
		return jogador;
	}

	public boolean getXeque() {
		return xeque;
	}

	public boolean getXequeMate() {
		return xequeMate;
	}

	public PecaDeXadrez getEnPassantPossivel() {
		return enPassantPossivel;
	}
	
	public PecaDeXadrez getPromocao() {
		return promocao;
	}
	
	public PecaDeXadrez[][] getPecas() {
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentoPossivel(PosicaoXadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosicao();
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaDeXadrez movimentoPecas(PosicaoXadrez posicaoOrigem, PosicaoXadrez posicaoDestino) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao destino = posicaoDestino.toPosicao();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca pecaCapiturada = movimentar(origem, destino);

		if (testeXeque(jogador)) {
			desfazerMovimento(origem, destino, pecaCapiturada);
			throw new XadrezException("Você nao pode se colocar em XEQUE");
		}

		PecaDeXadrez pecaMovimentada = (PecaDeXadrez)tabuleiro.peca(destino);
		
		//Movimeto especial Promoção
		promocao = null;
		if (pecaMovimentada instanceof Peao) {
			if((pecaMovimentada.getCor() == Cores.BRANCO && destino.getLinha() == 7) || (pecaMovimentada.getCor() == Cores.PRETO && destino.getLinha() == 0)) {
				promocao = (PecaDeXadrez)tabuleiro.peca(destino);
				promocao = pecaPromovida("RAINHA");
			}
		}
		
		xeque = (testeXeque(adversario(jogador))) ? true : false;

		if (testeXequeMate(adversario(jogador))) {
			xequeMate = true;
		} else {
			proximoTurno();
		}
		
		// Movimento especial En Passant
		if (pecaMovimentada instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantPossivel = pecaMovimentada;
		} else {
			enPassantPossivel = null;
		}
		
		return (PecaDeXadrez) pecaCapiturada;
	}

	public PecaDeXadrez pecaPromovida(String tipo) {
		if (promocao == null) {
			throw new IllegalStateException("Nao Existe peca para ser promovida");	
		}
		if (!tipo.equals("TORRE") && !tipo.equals("CAVALO") && !tipo.equals("BISPO") && !tipo.equals("RAINHA")) {
			return promocao;
		}
		
		Posicao pos = promocao.getPosicaoXadrez().toPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecaNoTabuleiro.remove(p);
		
		PecaDeXadrez novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.localPeca(novaPeca, pos);
		pecaNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	
	}
	
	private PecaDeXadrez novaPeca(String tipo, Cores cor) {
		if (tipo.equals("RAINHA")) return new Rainha(tabuleiro,cor);
		if (tipo.equals("TORRE")) return new Torre(tabuleiro,cor);
		if (tipo.equals("BISPO")) return new Bispo(tabuleiro,cor);
		return new Cavalo(tabuleiro,cor);
	}
	
	private Peca movimentar(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(origem);
		p.aumentaOContadorDeMovimentos();
		Peca pecaCapiturada = tabuleiro.removePeca(destino);
		tabuleiro.localPeca(p, destino);

		if (pecaCapiturada != null) {
			pecaNoTabuleiro.remove(pecaCapiturada);
			pecasCapituradas.add(pecaCapiturada);
		}
		
		// Movimento especial roque do lado do rei
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2 ) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.localPeca(torre, destinoT);
			torre.aumentaOContadorDeMovimentos();
		}
		
		// Movimento especial roque do lado da rainha
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2 ) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.localPeca(torre, destinoT);
			torre.aumentaOContadorDeMovimentos();
		}
		
		//Movimento especial En Passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapiturada == null){
				Posicao posicaoPeao;
				if (p.getCor() == Cores.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				pecaCapiturada = tabuleiro.removePeca(posicaoPeao);
				pecasCapituradas.add(pecaCapiturada);
				pecaNoTabuleiro.remove(pecaCapiturada);
				
			}
		}
		
		return pecaCapiturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapiturada) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(destino);
		p.diminuiOContadorDeMovimentos();
		tabuleiro.localPeca(p, origem);

		if (pecaCapiturada != null) {
			tabuleiro.localPeca(pecaCapiturada, destino);
			pecasCapituradas.remove(pecaCapiturada);
			pecaNoTabuleiro.add(pecaCapiturada);
		}
		
		// Movimento especial roque do lado do rei
			if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2 ) {
				Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
				Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
				PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(destinoT);
				tabuleiro.localPeca(torre, origemT);
				torre.diminuiOContadorDeMovimentos();
			}
			
			// Movimento especial roque do lado da rainha
				if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2 ) {
					Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
					Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
					PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(destinoT);
					tabuleiro.localPeca(torre, origemT);
					torre.diminuiOContadorDeMovimentos();
				}
				
				//Movimento especial En Passant
				if (p instanceof Peao) {
					if (origem.getColuna() != destino.getColuna() && pecaCapiturada == enPassantPossivel){
						PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removePeca(destino);
						Posicao posicaoPeao;
						if (p.getCor() == Cores.BRANCO) {
							posicaoPeao = new Posicao(4, destino.getColuna());
						} else {
							posicaoPeao = new Posicao(3, destino.getColuna());
						}
						
						tabuleiro.localPeca(peao, posicaoPeao);
						
					}
				}
	}

	private void validaPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.possuiPeca(posicao)) {
			throw new XadrezException("Não Possui peça na posição de Origem");
		}
		if (jogador != ((PecaDeXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException(" Esta peça não é sua");
		}
		if (!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
			throw new XadrezException("Não existe movimentos possiveis para peça escolhida");
		}
	}

	private void validaPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezException("A peça escolhida não pode se mover para posição de destino");
		}
	}

	private void proximoTurno() {
		turno++;
		jogador = (jogador == Cores.BRANCO) ? Cores.PRETO : Cores.BRANCO;
	}

	private Cores adversario(Cores cor) {
		return (cor == Cores.BRANCO) ? Cores.PRETO : Cores.BRANCO;
	}

	private PecaDeXadrez rei(Cores cor) {
		List<Peca> list = pecaNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaDeXadrez) p;
			}
		}
		throw new IllegalStateException("Não Existe um REI " + cor + " no tabuleiro");
	}

	private boolean testeXeque(Cores cor) {
		Posicao posicaoRei = rei(cor).getPosicaoXadrez().toPosicao();
		List<Peca> pecasAdversarias = pecaNoTabuleiro.stream()
				.filter(x -> ((PecaDeXadrez) x).getCor() == adversario(cor)).collect(Collectors.toList());
		for (Peca p : pecasAdversarias) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeXequeMate(Cores cor) {
		if (!testeXeque(cor)) {
			return false;
		}

		List<Peca> list = pecaNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaDeXadrez) p).getPosicaoXadrez().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapiturada = movimentar(origem, destino);
						boolean testeXeque = testeXeque(cor);
						desfazerMovimento(origem, destino, pecaCapiturada);
						if (!testeXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.localPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecaNoTabuleiro.add(peca);
	}

	private void iniciarPartida() {
		colocarNovaPeca('A', 8, new Torre(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('B', 8, new Cavalo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('C', 8, new Bispo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('D', 8, new Rainha(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('E', 8, new Rei(tabuleiro, Cores.BRANCO,this));
		colocarNovaPeca('F', 8, new Bispo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('G', 8, new Cavalo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('H', 8, new Torre(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('A', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('B', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('C', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('D', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('E', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('F', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('G', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		colocarNovaPeca('H', 7, new Peao(tabuleiro, Cores.BRANCO, this));
		
		
		colocarNovaPeca('A', 1, new Torre(tabuleiro, Cores.PRETO));
		colocarNovaPeca('B', 1, new Cavalo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('C', 1, new Bispo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('D', 1, new Rainha(tabuleiro, Cores.PRETO));
		colocarNovaPeca('E', 1, new Rei(tabuleiro, Cores.PRETO,this));
		colocarNovaPeca('F', 1, new Bispo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('G', 1, new Cavalo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('H', 1, new Torre(tabuleiro, Cores.PRETO));
		colocarNovaPeca('A', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('B', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('C', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('D', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('E', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('F', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('G', 2, new Peao(tabuleiro, Cores.PRETO, this));
		colocarNovaPeca('H', 2, new Peao(tabuleiro, Cores.PRETO, this));
		
	}

}
