package br.itcampos.buildyourhealth.model.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}