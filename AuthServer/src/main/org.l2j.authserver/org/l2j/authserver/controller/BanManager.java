/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.authserver.controller;

import org.l2j.commons.util.FileUtil;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;

/**
 * @author JoeAlisson
 */
class BanManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BanManager.class);
    private final Map<String, Long> bannedAdresses = new ConcurrentHashMap<>();

    private BanManager() {
        loadBanFile();
    }

    private void loadBanFile() {
        try(var reader = FileUtil.reader("banned_ip.cfg")) {
            reader.lines().filter(Util::isNotEmpty).forEach(this::addBannedAddress);
        } catch (IOException e) {
            LOGGER.warn("Error while reading the bans file ({}).", "banned_ip.cfg", e);
        }
    }

    private void addBannedAddress(String bannedInfo) {
        bannedInfo = bannedInfo.trim();
        if (bannedInfo.startsWith("#")) {
            return;
        }

        var infoParts = bannedInfo.split(" ");
        long expiration = -1;
        if (infoParts.length > 1) {
            try {
                expiration = Long.parseLong(infoParts[1]);
            } catch (NumberFormatException e) {
                LOGGER.warn("Skipped: Incorrect ban duration ({}) for address {}", infoParts[1], infoParts[0]);
            }
        }
        addBannedAdress(infoParts[0], expiration);
    }

    void addBannedAdress(String address, long expiration) {
        bannedAdresses.put(address, expiration);
    }

    boolean isBanned(String address) {
        var banned = bannedAdresses.containsKey(address);
        if(banned) {
            Long expiration = bannedAdresses.get(address);
            if((expiration > 0) && (expiration < currentTimeMillis())) {
                bannedAdresses.remove(address);
                return false;
            }
        }
        return banned;
    }

    public static BanManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BanManager INSTANCE = new BanManager();
    }
}
