package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Program {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		Scanner sc1 = new Scanner(System.in);
		Scanner sc2 = new Scanner(System.in);
		
		PartidaDeXadrez partida = new PartidaDeXadrez();
		List<PecaDeXadrez> capiturada = new ArrayList<>();
		String fim = "NAO";
		
		while (!partida.getXequeMate() || fim == "SIM") {
			try {

				UI.limpaTela();
				UI.mostraPartida(partida, capiturada);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.lerPosicaoXadrez(sc);

				boolean[][] movimentosPossiveis = partida.movimentoPossivel(origem);
				UI.limpaTela();
				UI.mostraTabuleiro(partida.getPecas(), movimentosPossiveis);
				;
				System.out.println();
				System.out.print("Destino: ");
				PosicaoXadrez destino = UI.lerPosicaoXadrez(sc);

				PecaDeXadrez pecaCapiturada = partida.movimentoPecas(origem, destino);
				
				if (pecaCapiturada != null) {
					capiturada.add(pecaCapiturada);
				}
				
				if (partida.getPromocao() != null) {
					System.out.print("Escolha a promoçao (RAINHA/BISPO/CAVALO/TORRE) :");
					String tipo = sc1.nextLine().toUpperCase();
					while (!tipo.equals("TORRE") && !tipo.equals("CAVALO") && !tipo.equals("BISPO") && !tipo.equals("RAINHA")) {
						System.out.print("Valor Invalido! Escolha a promoçao (RAINHA/BISPO/CAVALO/TORRE) : ");
						tipo = sc1.nextLine().toUpperCase();
					}
					partida.pecaPromovida(tipo);
				}
				
			} catch (XadrezException e) {
				System.out.print(e.getLocalizedMessage());
				sc1.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getLocalizedMessage());
				sc1.nextLine();
			}
		}
		UI.limpaTela();
		UI.mostraPartida(partida, capiturada);
		System.out.println("DESEJA INICIAR UMA NOVA PARTIDA? (SIM/NAO)");
		fim = sc1.next();
		if (fim == "SIM" || fim == "sim") {
			UI.limpaTela();
		}
		
		
	}
}