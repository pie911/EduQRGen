import os

def create_project_structure(base_path="EduQRGen"):
    structure = [
        ".gitignore",
        "pom.xml",
        "src/main/java/module-info.java",
        "src/main/java/com/eduqrgen/App.java",
        "src/main/java/com/eduqrgen/EduQRGen.java",
        "src/main/java/com/eduqrgen/PDFHandler.java",
        "src/main/java/com/eduqrgen/QRCodeGenerator.java",
        "src/main/java/com/eduqrgen/SystemInfo.java",
        "src/main/java/com/eduqrgen/database/QRDatabase.java",
        "src/main/resources/styles.css",
        "src/main/resources/sample.pdf",
        "src/test/java/com/eduqrgen/tests/",
        "target/"
    ]
    
    for path in structure:
        full_path = os.path.join(base_path, path)
        if path.endswith("/") or "." not in os.path.basename(path):
            os.makedirs(full_path, exist_ok=True)  # Create directories
        else:
            os.makedirs(os.path.dirname(full_path), exist_ok=True)
            with open(full_path, "w") as f:
                f.write(f"// Placeholder for {path}\n")
    
    print(f"Project structure created successfully at {base_path}")

if __name__ == "__main__":
    create_project_structure()
