class CreateCheckPaths < ActiveRecord::Migration
  def change
    create_table :check_paths do |t|
      t.text :description

      t.timestamps
    end
  end
end
