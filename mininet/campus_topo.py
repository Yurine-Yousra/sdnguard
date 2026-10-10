from mininet.topo import Topo

HOSTS_CAMPUS =  [
    ("h1", "10.0.1.1", 16, "00:00:00:00:00:01", "s1", 1),
    ("h2", "10.0.1.2", 16, "00:00:00:00:00:02", "s1", 2),
    ("h3", "10.0.2.1", 16, "00:00:00:00:00:03", "s2", 1),
    ("h4", "10.0.2.2", 16, "00:00:00:00:00:04", "s2", 2),
    ("h5", "10.0.3.1", 16, "00:00:00:00:00:05", "s3", 1),
    ("h6", "10.0.3.2", 16, "00:00:00:00:00:06", "s3", 2),
    ("h7", "10.0.4.1", 16, "00:00:00:00:00:07", "s4", 1),
    ("h8", "10.0.4.2", 16, "00:00:00:00:00:08", "s4", 2),
]

LINKS_CAMPUS = [
    ("s1", 3, "s5", 1),
    ("s1", 4, "s6", 1),
    ("s2", 3, "s5", 2),
    ("s2", 4, "s6", 2),
    ("s3", 3, "s6", 3),
    ("s3", 4, "s7", 1),
    ("s4", 3, "s6", 4),
    ("s4", 4, "s7", 2),
    ("s5", 3, "s6", 8),
    ("s5", 5, "s8", 1),
    ("s5", 4, "s9", 4),
    ("s6", 5, "s7", 3),
    ("s6", 6, "s9", 1),
    ("s6", 7, "s8", 2),
    ("s7", 4, "s8", 3),
    ("s7", 5, "s9", 2),
    ("s8", 4, "s9", 3),

]

class CampusTopo(Topo):
    def build(self):
        for i in range(1, 10):
            self.addSwitch(f"s{i}")


        for name,ip,subnet,mac,switch,port in HOSTS_CAMPUS:
            self.addHost(name, ip=f"{ip}/{subnet}",mac=mac)
            self.addLink(name, switch, port2=port)

        for sw_a,port_a,sw_b,port_b in LINKS_CAMPUS:
            self.addLink(sw_a, sw_b, port1=port_a, port2=port_b)

topos = {"campus": (lambda: CampusTopo())}