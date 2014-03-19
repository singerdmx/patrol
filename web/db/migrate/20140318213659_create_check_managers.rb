class CreateCheckManagers < ActiveRecord::Migration
  def change
    create_table :check_managers do |t|
      t.belongs_to :check_route
      t.belongs_to :asset
    end
  end
end
