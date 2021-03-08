package cz.smarteon.loxone.system.status

import cz.smarteon.loxone.app.MiniserverType
import cz.smarteon.loxone.message.SerializationSupport
import spock.lang.Specification

class MiniserverStatusTest extends Specification implements SerializationSupport {

    def "should deserialize"() {
        when:
        def ms = readResourceXml('system/status/status.xml', MiniserverStatus)

        then:
        ms
        ms.modified == '2019-09-04 13:02:36'
        ms.type == MiniserverType.REGULAR
        ms.name == 'test'
        ms.ip == '192.168.17.17'
        ms.mask == '255.255.255.0'
        ms.gateway == '192.168.17.1'
        ms.usesDhcp()
        ms.dns1 == '192.168.17.1'
        ms.dns2 == '8.8.8.8'
        ms.mac == '504F9411234'
        ms.device == 'TestDevice'
        ms.version == '10.2.3.26'
        ms.lanErrorsPercent == 0.0
        ms.linkErrorsCount == 0

        ms.extensions?.size() == 10
        ms.extensions[1] instanceof BasicExtension
        ms.extensions[1].name == 'Extension'
        ms.extensions[2].name == 'RS485 Extension'
        ms.extensions[2].online
        ms.extensions[4].name == 'DMX Extension'
        !ms.extensions[4].online
        ms.extensions[5].name == 'Dali Extension'
        ms.extensions[5].online
        ms.extensions[6].name == 'DI Extension'
        ms.extensions[6].online
        ms.extensions[7].name == 'Modbus Extension'
        ms.extensions[7].online
        ms.extensions[8].name == 'Dimmer Extension'
        ms.extensions[8].online
        ms.extensions[9] instanceof UnrecognizedExtension
        ms.extensions[9].name == 'Some Future Extension'

        def airBaseExtensions = ms.getExtensions(AirBaseExtension)
        airBaseExtensions?.size() == 1

        def airBase = airBaseExtensions[0]
        airBase.devices.size() == 2
        airBase.devices[0].name == 'VentilLeft'
        airBase.updating
        airBase.updateProgress == 89

        def treeExtensions = ms.getExtensions(TreeExtension)
        treeExtensions?.size() == 1
        treeExtensions[0].leftBranch.devices.size() == 2
        treeExtensions[0].rightBranch.devices[0].name == 'Linka'
        treeExtensions[0].rightBranch.devices[1].name == 'Tree to Air Bridge'
        def tree2ndLeft = treeExtensions[0].leftBranch.devices[1]
        tree2ndLeft.updating
        tree2ndLeft.updateProgress == 73

        def tree2airBridge = treeExtensions[0].rightBranch.devices[1] as TreeToAirBridge
        tree2airBridge.devices.size() == 1
        tree2airBridge.hwVersion == '100'
        tree2airBridge.mac == '50:4F:94:FF:FE:C0:07:95'
        !tree2airBridge.occupied
        !tree2airBridge.interfered
        def airUnderTree = tree2airBridge.devices[0]
        airUnderTree.updating
        airUnderTree.updateProgress == 57

        def daliExtensions = ms.getExtensions(DaliExtension)
        daliExtensions?.size() == 1
        daliExtensions[0].devices.size() == 1
        daliExtensions[0].devices[0].serialNumber == '0:83213221'
    }
}
