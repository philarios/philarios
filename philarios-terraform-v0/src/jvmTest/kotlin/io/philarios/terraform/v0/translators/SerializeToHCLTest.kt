package io.philarios.terraform.v0.translators

import io.philarios.terraform.v0.*
import org.amshove.kluent.`should equal`
import kotlin.test.Test

class SerializeToHCLTest {

    @Test
    fun `create an empty string for an empty configuration`() {
        val configuration = Configuration(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        val actual = configuration.serializeToHCL()
        actual `should equal` ""
    }

    @Test
    fun `create string with config list for a resource with config`() {
        val resource = Resource("aws_instance", "web", mapOf(
                "ami" to "ami-408c7f28",
                "instance_type" to "t1.micro"
        ))
        val actual = resource.serializeToHCL()
        actual `should equal` """
            resource "aws_instance" "web" {
              ami = "ami-408c7f28"
              instance_type = "t1.micro"
            }
        """.trimIndent()
    }

    @Test
    fun `create string with config list for a data source with empty config`() {
        val dataSource = DataSource("aws_ami", "web", emptyMap())
        val actual = dataSource.serializeToHCL()
        actual `should equal` """
            data "aws_ami" "web" {

            }
        """.trimIndent()
    }

    @Test
    fun `create string with config list for a provider with config`() {
        val provider = Provider("aws", mapOf(
                "access_key" to "foo",
                "secret_key" to "bar",
                "region" to "us-east-1"
        ))
        val actual = provider.serializeToHCL()
        actual `should equal` """
            provider "aws" {
              access_key = "foo"
              secret_key = "bar"
              region = "us-east-1"
            }
        """.trimIndent()
    }

    @Test
    fun `create string with config list for a variable with type but no default`() {
        val variable = Variable("key", "string", "")
        val actual = variable.serializeToHCL()
        actual `should equal` """
            variable "key" {
              type = "string"
            }
        """.trimIndent()
    }

    @Test
    fun `create string with config list for a output with value`() {
        val output = Output("address", "\${aws_instance.db.public_dns}")
        val actual = output.serializeToHCL()
        actual `should equal` """
            output "address" {
              value = "${"$"}{aws_instance.db.public_dns}"
            }
        """.trimIndent()
    }

}
