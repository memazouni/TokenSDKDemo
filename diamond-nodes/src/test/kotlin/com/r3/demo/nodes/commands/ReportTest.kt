package com.r3.demo.nodes.commands

import com.r3.demo.nodes.Main
import com.r3.demo.nodes.User
import net.corda.client.rpc.RPCConnection
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.CordaRPCOps
import net.corda.testing.core.TestIdentity
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class ReportTest {
    private val ALICE = TestIdentity(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"))
    private val GIC = TestIdentity(CordaX500Name(organisation = "GIC", locality = "TestCity", country = "US"))

    @Test
    fun parseParameters() {
        val main = Mockito.mock(Main::class.java)
        val service = Mockito.mock(CordaRPCOps::class.java)
        val connection = TestConnection(service)

        Mockito.`when`(main.getConnection(any(User::class.java))).thenReturn(connection)

        val requester = User(main, "alice", "userA", "","","")
        val assessor = User(main, "gic", "userB", "","","")

        Mockito.`when`(main.getUser(eq("alice"))).thenReturn(requester)
        Mockito.`when`(main.getUser(eq("gic"))).thenReturn(assessor)
        Mockito.`when`(main.getWellKnownUser(eq(requester), any(CordaRPCOps::class.java))).thenReturn(ALICE.party)
        Mockito.`when`(main.getWellKnownUser(eq(assessor), any(CordaRPCOps::class.java))).thenReturn(GIC.party)

        val text = "create alice (gic, alice, 1.0, VVS2, E, 'EX')"
        val report = Utilities.parseReport(main, service, text)

        assertEquals(ALICE.party, report.requester)
        assertEquals(GIC.party, report.assessor)
    }

    fun <T> eq(obj: T): T = Mockito.eq<T>(obj)
    fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    class TestConnection(val service: CordaRPCOps) : RPCConnection<CordaRPCOps> {
        override val proxy: CordaRPCOps get() = service
        override val serverProtocolVersion: Int get() = 0

        override fun forceClose() {
        }

        override fun notifyServerAndClose() {
        }

        override fun close() {
        }
    }
}