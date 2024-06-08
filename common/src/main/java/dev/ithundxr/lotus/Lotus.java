/*
 * Lotus
 * Copyright (c) 2024 IThundxr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.ithundxr.lotus;

import dev.yumi.commons.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Lotus {
	public static final String MOD_ID = "lotus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final EventManager<String> EVENTS = new EventManager<>("default", Function.identity());

	public static void init() {
		LOGGER.info("Initializing Lotus API");
	}
}
