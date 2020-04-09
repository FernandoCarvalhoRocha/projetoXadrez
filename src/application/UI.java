package application;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import xadrez.Cores;
import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;

public class UI {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	public static void limpaTela() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static PosicaoXadrez lerPosicaoXadrez(Scanner sc) {
		try {
			String s = sc.next().toUpperCase();
			char coluna = s.charAt(0);
			int linha = Integer.parseInt(s.substring(1));
			return new PosicaoXadrez(coluna, linha);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Erro ao ler a posição. Valores validos vão de A1 a H8.");
		}

	}

	public static void mostraPartida(PartidaDeXadrez partida, List<PecaDeXadrez> capiturada) {
		mostraTabuleiro(partida.getPecas());
		System.out.println();
		mostraPecaCapturada(capiturada);
		System.out.println();
		System.out.println("Turno: " + partida.getTurno());
		if (!partida.getXequeMate()) {
			System.out.println("Esperando o jogador: " + partida.getJogador());
			if (partida.getXeque()) {
				System.out.println("VOCÊ ESTÁ EM XEQUE!");
			}
		} else {
			System.out.println("XEQUE MATE");
			System.out.println("VENCEDOR: " + partida.getJogador());
			
		}

	}

	public static void mostraTabuleiro(PecaDeXadrez[][] pecas) {
		for (int i = 0; i < pecas.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pecas.length; j++) {
				mostraPeca(pecas[i][j], false);
			}
			System.out.println();
			System.out.println();
		}
		System.out.println("     A       B       C       D       E       F       G       H");
	}

	public static void mostraTabuleiro(PecaDeXadrez[][] pecas, boolean[][] movimentosPossiveis) {
		for (int i = 0; i < pecas.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pecas.length; j++) {
				mostraPeca(pecas[i][j], movimentosPossiveis[i][j]);
			}
			System.out.println();
			System.out.println();
		}
		System.out.println("     A       B       C       D       E       F       G       H");
	}

	private static void mostraPeca(PecaDeXadrez peca, boolean fundoPeca) {
		if (fundoPeca) {
			System.out.print(ANSI_GREEN_BACKGROUND);
		}
		if (peca == null) {
			System.out.print("-------" + ANSI_RESET);
		} else {
			if (peca.getCor() == Cores.BRANCO) {
				System.out.print(ANSI_WHITE + peca + ANSI_RESET);
			} else {
				System.out.print(ANSI_YELLOW + peca + ANSI_RESET);
			}
		}
		System.out.print(" ");
	}

	private static void mostraPecaCapturada(List<PecaDeXadrez> capiturada) {
		List<PecaDeXadrez> branca = capiturada.stream().filter(x -> x.getCor() == Cores.BRANCO)
				.collect(Collectors.toList());
		List<PecaDeXadrez> preta = capiturada.stream().filter(x -> x.getCor() == Cores.PRETO)
				.collect(Collectors.toList());
		System.out.println("peças Capturadas: ");
		System.out.print("Brancas: ");
		System.out.print(ANSI_WHITE);
		System.out.print(Arrays.toString(branca.toArray()));
		System.out.println(ANSI_RESET);
		System.out.print("Pretas: ");
		System.out.print(ANSI_YELLOW);
		System.out.print(Arrays.toString(preta.toArray()));
		System.out.println(ANSI_RESET);

	}
}
