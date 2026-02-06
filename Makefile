APP_NAME = ProResConverter
INSTALL_DIR = $(HOME)/Apps/$(APP_NAME)
DESKTOP_DIR = $(HOME)/.local/share/applications
DESKTOP_FILE = $(DESKTOP_DIR)/prores-converter.desktop
JAR_SOURCE = target/VideoConverter-1.0-SNAPSHOT.jar
ICON_SOURCE = src/main/resources/images/app_logo.png
JAVA_EXEC := $(shell which java)

.PHONY: all package install uninstall clean run

all: package

package:
	./mvnw clean package

install: package
	@echo "--- Installing $(APP_NAME) ---"
	mkdir -p $(INSTALL_DIR)
	
	cp $(JAR_SOURCE) $(INSTALL_DIR)/app.jar
	cp $(ICON_SOURCE) $(INSTALL_DIR)/icon.png
	
	@echo "--- Creating Menu Shortcut ---"
	
	@echo "[Desktop Entry]" > $(DESKTOP_FILE)
	@echo "Type=Application" >> $(DESKTOP_FILE)
	@echo "Name=ProRes Converter" >> $(DESKTOP_FILE)
	@echo "Comment=FFmpeg Video Converter with JavaFX Interface" >> $(DESKTOP_FILE)
	@echo "Exec=$(JAVA_EXEC) -jar $(INSTALL_DIR)/app.jar" >> $(DESKTOP_FILE)
	@echo "Icon=$(INSTALL_DIR)/icon.png" >> $(DESKTOP_FILE)
	@echo "Categories=AudioVideo;Utility;Java;" >> $(DESKTOP_FILE)
	@echo "Terminal=false" >> $(DESKTOP_FILE)
	@echo "StartupWMClass=com.videoconverter.Launcher" >> $(DESKTOP_FILE)
	
	chmod +x $(DESKTOP_FILE)
	update-desktop-database $(DESKTOP_DIR)
	
	@echo "--- Installation Completed Successfully! ---"
	@echo "Search for 'ProRes Converter' in your application menu."


uninstall:
	@echo "--- Uninstalling $(APP_NAME) ---"
	rm -rf $(INSTALL_DIR)
	rm -f $(DESKTOP_FILE)
	update-desktop-database $(DESKTOP_DIR)
	@echo "--- Removed. ---"

run: package
	java -jar $(JAR_SOURCE)

clean:
	./mvnw clean