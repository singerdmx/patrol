class CreateCheckRoutes < ActiveRecord::Migration
  def change
    create_table :check_routes do |t|
      t.text :description
      t.timestamps
    end
  end
end
