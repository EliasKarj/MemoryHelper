<div align="center">

# Muistikaveri

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Gemini](https://img.shields.io/badge/Gemini%20AI-8E75C2?style=for-the-badge&logo=googlegemini&logoColor=white)
![Status](https://img.shields.io/badge/Status-Development-yellow?style=for-the-badge)

<br />

**Älykäs avustaja ja turvaverkko muistisairaille sekä heidän läheisilleen**
<br />

</div>

## Ominaisuudet

-  **Rutiinien hallinta:** Selkeä ja helppokäyttöinen näkymä päivittäisille tehtäville (lääkkeet, ruokailu jne.) suurilla painikkeilla ja selkeällä visuaalisella palautteella.
-  **Omaisen seuranta:** Erillinen rooli läheiselle, joka voi seurata potilaan tilaa, sijaintia ja päivän rutiinien toteutumista reaaliajassa.
-  **Turva-alueet (Geofencing):** Taustalla toimiva palvelu, joka tarkkailee potilaan sijaintia suhteessa kotiin ja hälyttää, jos potilas poistuu määritellyltä turva-alueelta.
-  **Vointi-chat (AI):** Google Gemini AI -pohjainen keskusteluavustaja, joka tukee potilasta arkisissa asioissa ja seuraa vointia luonnollisen kielen avulla.
-  **Sijaintipalvelut:** Integroitu Google Maps -tuki kodin sijainnin määrittämiseen ja reaaliaikaiseen paikannukseen.
-  **Roolipohjainen käyttöliittymä:** Yksi sovellus, kaksi eri käyttökokemusta (Potilas vs. Omainen) tarpeiden mukaan.

---

##  Teknologiat

Tämä projekti on rakennettu moderneilla Android-kehityksen standardeilla:

| Osa-alue | Teknologia |
| :--- | :--- |
| **Kieli** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Tietokanta** | Firebase Firestore |
| **Tekoäly** | Google Gemini (Generative AI SDK) |
| **Sijainti** | Google Play Services Location & Geofencing |
| **Arkkitehtuuri** | MVVM (ViewModel, StateFlow) |
| **Taustatyöt** | Android Foreground Services |

---

## Asennus ja käyttöönotto

1. **Kloonaa projekti:**
   ```bash
   git clone https://github.com/kayttaja/muistikaveri.git
   ```

2. **Firebase konfigurointi:**
   - Luo uusi projekti [Firebase Consolessa](https://console.firebase.google.com/).
   - Lisää Android-sovellus ja lataa `google-services.json` projektin `app/` kansioon.
   - Ota käyttöön Firestore Database.

3. **API-avaimet:**
   - Lisää Gemini API-avain `local.properties` tiedostoon tai suoraan koodiin (testausvaihe).
   - Varmista että Google Maps SDK on aktivoitu Google Cloud Consolessa.

4. **Aja sovellus:**
   - Avaa projekti Android Studiossa (Ladybug tai uudempi suositeltu).
   - Build & Run!

---

