import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748b"))
        
        # Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(54, 755, "Real-Time Bus Tracking System — Project Viva & Explanation Guide")
            self.setStrokeColor(colors.HexColor("#cbd5e1"))
            self.setLineWidth(0.5)
            self.line(54, 748, 558, 748)
            
        # Footer
        footer_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(558, 36, footer_text)
        self.drawString(54, 36, "Academic Project Guide • Sathyabama Institute of Science and Technology • CSE")
        self.setStrokeColor(colors.HexColor("#cbd5e1"))
        self.setLineWidth(0.5)
        self.line(54, 48, 558, 48)
        self.restoreState()

def build_pdf(filename="Real-Time_Bus_Tracking_System_Project_Explanation_Guide.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()
    
    c_primary = colors.HexColor("#1e3a8a")     # Deep Navy
    c_secondary = colors.HexColor("#2563eb")   # Royal Blue
    c_dark = colors.HexColor("#0f172a")        # Text Dark
    c_muted = colors.HexColor("#475569")       # Text Muted
    c_light = colors.HexColor("#f8fafc")       # Card Background
    c_border = colors.HexColor("#cbd5e1")      # Border Grey

    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=c_primary,
        spaceAfter=4
    )

    subtitle_style = ParagraphStyle(
        'DocSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        leading=14,
        textColor=c_muted,
        spaceAfter=10
    )

    h1_style = ParagraphStyle(
        'SectionH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=c_primary,
        spaceBefore=10,
        spaceAfter=5
    )

    body_style = ParagraphStyle(
        'BodyDark',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=c_dark,
        spaceAfter=4
    )

    body_bold = ParagraphStyle(
        'BodyBold',
        parent=body_style,
        fontName='Helvetica-Bold'
    )

    bullet_style = ParagraphStyle(
        'BulletText',
        parent=body_style,
        leftIndent=12,
        spaceAfter=3
    )

    q_style = ParagraphStyle(
        'VivaQ',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9.5,
        leading=13,
        textColor=c_primary,
        spaceBefore=4,
        spaceAfter=1
    )

    a_style = ParagraphStyle(
        'VivaA',
        parent=body_style,
        fontName='Helvetica',
        leftIndent=10,
        spaceAfter=5
    )

    story = []

    # ================= PAGE 1 =================
    story.append(Paragraph("Real-Time Bus Tracking System", title_style))
    story.append(Paragraph("<b>Student & Client Presentation Guide</b> — Simple, Plain English Explanation for Teachers & Viva Voce", subtitle_style))
    story.append(HRFlowable(width="100%", thickness=1.5, color=c_secondary, spaceBefore=0, spaceAfter=10))

    # SECTION 1
    story.append(Paragraph("1. How to Explain This Project in 30 Seconds (The Elevator Pitch)", h1_style))
    story.append(Paragraph(
        "<i>\"Good morning/afternoon, Teacher. Our project is a <b>Real-Time Bus Tracking System</b> built 100% with Java and Spring Boot. "
        "It solves the problem of commuters and university students waiting aimlessly at bus stops without knowing where their bus is. "
        "Our system shows the exact live location of buses moving on an interactive map, calculates dynamic arrival times (ETA) using "
        "the mathematical Haversine formula, displays how crowded each bus is, and updates automatically in real time without refreshing the webpage.\"</i>",
        body_style
    ))
    story.append(Spacer(1, 6))

    # SECTION 2
    story.append(Paragraph("2. The Problem vs. Our Solution", h1_style))
    prob_data = [
        [Paragraph("<b>The Real-World Problem</b>", body_bold), Paragraph("<b>How Our System Solves It</b>", body_bold)],
        [
            Paragraph("• Students wait long hours because public/college buses have unpredictable schedules.<br/>"
                      "• Static paper timetables don't show real-time traffic delays or breakdowns.<br/>"
                      "• Students board already overcrowded buses without warning.", body_style),
            Paragraph("• Live bus icon moves smoothly on OpenStreetMap every 2 seconds.<br/>"
                      "• Dynamic ETA countdown (e.g., <i>\"Arriving in 6 mins\"</i>) based on vehicle speed.<br/>"
                      "• Real-time crowd indicator (Low, Moderate, Crowded) showing available seats.", body_style)
        ]
    ]
    t_prob = Table(prob_data, colWidths=[240, 264])
    t_prob.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#e0e7ff")),
        ('GRID', (0,0), (-1,-1), 0.5, c_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('PADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_prob)
    story.append(Spacer(1, 8))

    # SECTION 3
    story.append(Paragraph("3. The 3 User Roles (Who Uses the System?)", h1_style))
    story.append(Paragraph("To explain this easily to your teacher, compare it to <b>Uber</b>: an Uber app needs a passenger, a driver, and the company manager. Our transit system has the exact same 3 roles:", body_style))

    roles_data = [
        [Paragraph("<b>User Role</b>", body_bold), Paragraph("<b>Real-World Person</b>", body_bold), Paragraph("<b>What They Do in the Application</b>", body_bold)],
        [
            Paragraph("<b>Passenger</b><br/>(Student)", body_style),
            Paragraph("Student or commuter at the bus stop", body_style),
            Paragraph("• <b>No login needed</b> (public web page).<br/>• Opens the live map to watch their bus moving.<br/>• Checks arrival countdown (ETA) & seat availability.", body_style)
        ],
        [
            Paragraph("<b>Driver</b>", body_style),
            Paragraph("Bus driver inside the vehicle", body_style),
            Paragraph("• Logs into the onboard tablet (`driver1`).<br/>• Sees their assigned route and scheduled stops.<br/>• Clicks <b>\"Start Trip\"</b> and <b>\"Finish Trip\"</b>.<br/>• Transmits live GPS beacon coordinates.", body_style)
        ],
        [
            Paragraph("<b>Admin</b>", body_style),
            Paragraph("College Transport Manager", body_style),
            Paragraph("• Full management portal (`admin`).<br/>• Adds/edits buses, routes, drivers, and trips.<br/>• Controls the built-in <b>Java GPS Simulator</b> (Start, Stop, Speed 1x–5x).", body_style)
        ]
    ]
    t_roles = Table(roles_data, colWidths=[100, 130, 274])
    t_roles.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#e2e8f0")),
        ('GRID', (0,0), (-1,-1), 0.5, c_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('PADDING', (0,0), (-1,-1), 4.5),
    ]))
    story.append(t_roles)

    # ================= PAGE 2 =================
    story.append(PageBreak())

    story.append(Paragraph("4. Technology Stack (Why Did We Choose These?)", h1_style))
    story.append(Paragraph("If the teacher asks <i>\"Why did you choose this technology stack?\"</i>, use these exact points:", body_style))

    tech_data = [
        [Paragraph("<b>Technology</b>", body_bold), Paragraph("<b>Component</b>", body_bold), Paragraph("<b>Why It Was Used (Teacher Explanation)</b>", body_bold)],
        [
            Paragraph("<b>Java 17+</b>", body_style),
            Paragraph("Core Language", body_style),
            Paragraph("Mandated by curriculum for strong Object-Oriented Programming (OOP), multi-threading, robustness, and enterprise type safety.", body_style)
        ],
        [
            Paragraph("<b>Spring Boot 3.3</b>", body_style),
            Paragraph("Backend Framework", body_style),
            Paragraph("Provides built-in web server (Tomcat), dependency injection, REST APIs, and production-ready security without tedious XML setup.", body_style)
        ],
        [
            Paragraph("<b>Thymeleaf + HTML/CSS</b>", body_style),
            Paragraph("Frontend UI", body_style),
            Paragraph("Server-side Java templating. Ensures the application is <b>100% Java-based</b> without needing complex JavaScript frameworks like React or Node.js.", body_style)
        ],
        [
            Paragraph("<b>Spring WebSocket (STOMP)</b>", body_style),
            Paragraph("Live Updates", body_style),
            Paragraph("Enables bi-directional communication so the server pushes coordinates to the map instantly without requiring the user to reload the page.", body_style)
        ],
        [
            Paragraph("<b>MySQL & Spring Data JPA</b>", body_style),
            Paragraph("Database & ORM", body_style),
            Paragraph("Persistent relational storage for buses, routes, stops, and historical location telemetry. H2 database is included for zero-config demos.", body_style)
        ],
        [
            Paragraph("<b>Leaflet + OpenStreetMap</b>", body_style),
            Paragraph("Map Engine", body_style),
            Paragraph("100% open-source and free. <b>Requires zero paid API keys</b> and no credit card (unlike Google Maps API which is paid).", body_style)
        ]
    ]
    t_tech = Table(tech_data, colWidths=[110, 100, 294])
    t_tech.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#e2e8f0")),
        ('GRID', (0,0), (-1,-1), 0.5, c_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('PADDING', (0,0), (-1,-1), 4.5),
    ]))
    story.append(t_tech)
    story.append(Spacer(1, 8))

    story.append(Paragraph("5. How the System Actually Works (Behind the Scenes)", h1_style))
    story.append(Paragraph("Here is the technical flow explained simply in 4 easy steps:", body_style))
    
    story.append(Paragraph("<b>Step 1: Coordinate Generation (The GPS Simulator)</b><br/>"
                           "Real physical GPS hardware is expensive and impractical inside a classroom. We implemented a multithreaded "
                           "Java service (<code>GpsSimulatorService.java</code>) that runs every 2 seconds on a background thread. It takes real transit "
                           "coordinates (Chennai Central Station ➔ Guindy ➔ OMR ➔ Sathyabama Campus) and smoothly moves the simulated bus along that road path.", bullet_style))
    
    story.append(Paragraph("<b>Step 2: Mathematical Distance & ETA Calculation</b><br/>"
                           "Instead of guessing, our Java code uses the mathematical <b>Great-Circle Haversine Formula</b>. "
                           "It calculates the curved spherical distance between the bus's current latitude/longitude and upcoming stops. "
                           "Dividing distance by current vehicle speed calculates the accurate <b>Estimated Time of Arrival (ETA)</b> in minutes.", bullet_style))

    story.append(Paragraph("<b>Step 3: Instant Real-Time Push (WebSocket)</b><br/>"
                           "Whenever the bus coordinates change, the server broadcasts a message to the WebSocket channel <code>/topic/bus-locations</code>. "
                           "Every connected browser receives the new coordinate instantly and glides the bus marker forward on the map without refreshing.", bullet_style))

    story.append(Paragraph("<b>Step 4: Passenger Capacity & Crowding Detection</b><br/>"
                           "Each bus tracks capacity and passenger count. When simulated buses arrive at designated stops, passengers realistically board and alight. "
                           "The system automatically classifies the crowd level as <i>Low Crowding</i> (&lt;60%), <i>Moderate</i> (60%–85%), or <i>Crowded</i> (&gt;85%).", bullet_style))

    # ================= PAGE 3 =================
    story.append(PageBreak())

    story.append(Paragraph("6. Top Viva / Teacher Questions & Plain English Answers", h1_style))
    story.append(Paragraph("Memorize or refer to these answers during your evaluation:", body_style))

    qas = [
        ("Q1: Where do the GPS coordinates come from if you don't have physical hardware?",
         "Answer: We built a multithreaded Java GPS simulator. It loads predefined real-world geographic coordinates along actual campus routes. A Java ScheduledExecutorService calculates smooth movement between waypoints and broadcasts periodic updates every 2 seconds. The system also supports real GPS coordinates via driver pings."),

        ("Q2: How does the map update without refreshing the page?",
         "Answer: We use Spring WebSocket with STOMP protocol. When the Java server generates a new bus position, it pushes the data over an active WebSocket connection directly to the browser. The lightweight Leaflet JavaScript listener receives the coordinates and moves the bus pin smoothly on the map."),

        ("Q3: How do you calculate the arrival time (ETA)?",
         "Answer: In Java, we implemented the Haversine Formula in HaversineDistanceCalculator.java. It calculates the spherical distance in kilometers between the bus's current latitude/longitude and each upcoming stop. Then, using ETA = Distance / Speed, it computes remaining minutes and formats the estimated clock arrival time."),

        ("Q4: Why didn't you use React or Angular for the frontend?",
         "Answer: The academic specification strictly required a pure Java-centric enterprise architecture. We used Thymeleaf templates with HTML5 and CSS, which renders cleanly on the server and keeps all business logic, data models, and tracking services strictly inside Java."),

        ("Q5: Does this project require paid Google Maps API keys?",
         "Answer: No. Google Maps requires a paid Google Cloud account and billing setup. We integrated Leaflet with OpenStreetMap tiles, which is 100% free, open-source, and never expires. All routing and geodetic math is performed internally by our own Java code, so zero external API keys are required."),

        ("Q6: How is security handled?",
         "Answer: We used Spring Security 6. Passwords are encrypted with BCrypt hashing. We implemented Role-Based Access Control (RBAC): the public live map is accessible to all students without login, while administrative fleet management and driver consoles require authenticated credentials.")
    ]

    for q, a in qas:
        story.append(Paragraph(q, q_style))
        story.append(Paragraph(a, a_style))

    # ================= PAGE 4 =================
    story.append(PageBreak())

    story.append(Paragraph("7. Step-by-Step Live Demonstration Walkthrough", h1_style))
    story.append(Paragraph("Follow these exact steps when demonstrating the project in front of your teacher:", body_style))
    story.append(Spacer(1, 4))

    demo_steps = [
        [Paragraph("<b>Step</b>", body_bold), Paragraph("<b>What to Do</b>", body_bold), Paragraph("<b>What to Tell the Teacher (Script)</b>", body_bold)],
        [
            Paragraph("<b>1. Start App</b>", body_style),
            Paragraph("Run command in terminal:<br/><code>java -jar target/realtime-bus-tracking-system-1.0.0.jar --spring.profiles.active=h2</code>", body_style),
            Paragraph("<i>\"The application starts self-contained on port 8080. It automatically initializes database tables and seeds demo buses, routes, and user accounts.\"</i>", body_style)
        ],
        [
            Paragraph("<b>2. Student View</b>", body_style),
            Paragraph("Open browser to:<br/><b>http://localhost:8080/track</b>", body_style),
            Paragraph("<i>\"Here is the commuter map. As you can see, BUS-101 and BUS-102 are moving in real time along the OMR corridor without reloading the page. Clicking a bus shows its speed, next stop, and ETA countdown.\"</i>", body_style)
        ],
        [
            Paragraph("<b>3. Routes View</b>", body_style),
            Paragraph("Click <b>Routes</b> in navbar<br/>(<code>http://localhost:8080/routes</code>)", body_style),
            Paragraph("<i>\"Here are the official transit corridors. Clicking 'View Stop Schedule' shows the full vertical timeline of all ordered stops, distances, and scheduled timings.\"</i>", body_style)
        ],
        [
            Paragraph("<b>4. Admin View</b>", body_style),
            Paragraph("Login at <code>/login</code> with:<br/>Username: <code>admin</code><br/>Password: <code>admin123</code>", body_style),
            Paragraph("<i>\"This is the Admin Fleet Command. Admins can add buses, schedule trips, and adjust the GPS simulator speed (e.g., 2x or 5x) in the simulation control room.\"</i>", body_style)
        ],
        [
            Paragraph("<b>5. Driver View</b>", body_style),
            Paragraph("Login as:<br/>Username: <code>driver1</code><br/>Password: <code>driver123</code>", body_style),
            Paragraph("<i>\"This is the driver's onboard console. The driver can start and finish trips and transmit manual or device GPS sensor pings directly to the server.\"</i>", body_style)
        ]
    ]
    t_demo = Table(demo_steps, colWidths=[70, 200, 234])
    t_demo.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#e0e7ff")),
        ('GRID', (0,0), (-1,-1), 0.5, c_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('PADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(t_demo)
    story.append(Spacer(1, 14))

    story.append(Paragraph("8. Default Demo Credentials Quick Reference", h1_style))
    cred_data = [
        [Paragraph("<b>Role</b>", body_bold), Paragraph("<b>Username</b>", body_bold), Paragraph("<b>Password</b>", body_bold), Paragraph("<b>Landing Page</b>", body_bold)],
        [Paragraph("Fleet Administrator", body_style), Paragraph("<code>admin</code>", body_style), Paragraph("<code>admin123</code>", body_style), Paragraph("<code>/admin/dashboard</code>", body_style)],
        [Paragraph("Transit Driver", body_style), Paragraph("<code>driver1</code>", body_style), Paragraph("<code>driver123</code>", body_style), Paragraph("<code>/driver/dashboard</code>", body_style)],
        [Paragraph("Student / Commuter", body_style), Paragraph("<code>student1</code>", body_style), Paragraph("<code>student123</code>", body_style), Paragraph("<code>/track</code> (Public)", body_style)]
    ]
    t_cred = Table(cred_data, colWidths=[130, 90, 100, 184])
    t_cred.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#e2e8f0")),
        ('GRID', (0,0), (-1,-1), 0.5, c_border),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('PADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_cred)

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Successfully generated: {filename}")

if __name__ == "__main__":
    out_file = "Real-Time_Bus_Tracking_System_Project_Explanation_Guide.pdf"
    if len(sys.argv) > 1:
        out_file = sys.argv[1]
    build_pdf(out_file)
