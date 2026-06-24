package io.github.vvb2060.ims.model

data class NetworkExitStatus(
    val ip: String,
    val ipVersion: String,
    val country: String,
    val region: String,
    val city: String,
    val org: String,
    val risk: String,
    val googleReachable: Boolean?,
    val tiktokReachable: Boolean?,
    val captivePortalReachable: Boolean?,
)

data class ConfigBackupSnapshot(
    val id: String,
    val name: String,
    val createdAtMillis: Long,
    val subId: Int,
    val simTitle: String,
    val mcc: String,
    val mnc: String,
    val countryIso: String,
    val featureValues: Map<Feature, FeatureValue>,
    val countryMccOverride: String = "",
)

data class ApnDraftConfig(
    val name: String,
    val apn: String,
    val type: String,
    val mcc: String,
    val mnc: String,
)

object SupportRules {
    fun normalizeMcc(value: String): String {
        return value.filter { it.isDigit() }.take(3)
    }

    fun normalizeMnc(value: String): String {
        val digits = value.filter { it.isDigit() }.take(3)
        return if (digits.length == 1) digits.padStart(2, '0') else digits
    }

    fun requiresBackupMismatchConfirmation(
        backup: ConfigBackupSnapshot,
        currentMcc: String,
        currentMnc: String,
    ): Boolean {
        val backupMcc = normalizeMcc(backup.mcc)
        val backupMnc = normalizeMnc(backup.mnc)
        val selectedMcc = normalizeMcc(currentMcc)
        val selectedMnc = normalizeMnc(currentMnc)
        return backupMcc.isBlank() ||
            backupMnc.isBlank() ||
            selectedMcc.isBlank() ||
            selectedMnc.isBlank() ||
            backupMcc != selectedMcc ||
            backupMnc != selectedMnc
    }

    fun validateApnDraft(config: ApnDraftConfig): String? {
        val mcc = normalizeMcc(config.mcc)
        val mnc = normalizeMnc(config.mnc)
        return when {
            config.name.isBlank() -> "APN name is blank"
            config.apn.isBlank() -> "APN is blank"
            config.type.isBlank() -> "APN type is blank"
            mcc.length != 3 -> "MCC must be 3 digits"
            mnc.length !in 2..3 -> "MNC must be 2 or 3 digits"
            else -> null
        }
    }
}
