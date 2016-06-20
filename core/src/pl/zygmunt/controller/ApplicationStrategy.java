package pl.zygmunt.controller;

import pl.zygmunt.events.ApplicationEvent;

/**
 * Abstrakcyjna klasa bazowa dla klas reprezentujacych strategie ( odpowiednie
 * dzialania ) na dane zdarzenia.
 * 
 * @author Piotr Zygmunt
 */
public abstract class ApplicationStrategy
{
	/**
	 * Abstrakcyjna funkcja która uruchamiana jest jako reakcja na dane
	 * zdarzenie
	 * 
	 * @param evnt
	 *            Referencja na zdarzenie.
	 */
	abstract void execute(final ApplicationEvent evnt);
}
